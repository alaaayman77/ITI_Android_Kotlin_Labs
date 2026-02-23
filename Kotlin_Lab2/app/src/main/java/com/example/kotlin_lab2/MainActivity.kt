package com.example.kotlin_lab2
import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale


const val TAG = "MainActivity"
const val REQUEST_PERMISSION_CODE = 2005

class MainActivity : ComponentActivity() {
    private lateinit var fusedClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    lateinit var locationState: MutableState<Location>
    lateinit var addressState  : MutableState<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder=Geocoder(this, Locale.getDefault())
        enableEdgeToEdge()
        setContent {
            addressState = remember { mutableStateOf("No address yet") }
            locationState = remember { mutableStateOf(Location("")) }
            LocationDetails(locationState.value , addressState.value ,
                onOpenSms = { phone, msg ->
                    openSms(phone, msg)
                },
                onOpenMap = {lat , lng -> openMap(lat , lng)})

        }
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()

            } else {
                enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
                ),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    fun updateAddress(location: Location) {

        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                addressState.value =
                    "${address.locality}, ${address.countryName}"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            addressState.value = "Address not found"
        }
    }
    fun checkPermissions(): Boolean {
        var result = false
        if ((ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            result = true
        }
        return result
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    override
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            deviceId
        )
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    getFreshLocation()

                } else {
                    enableLocationServices()
                }
            }

        }
    }

    fun enableLocationServices() {
        Toast.makeText(this, "Turn on Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun isLocationEnabled(): Boolean {
        val locationManger: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManger.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    fun getFreshLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    Log.i(TAG, locationResult.lastLocation.toString())
                    locationState.value = locationResult.lastLocation ?: Location("")
                    updateAddress(locationState.value)
                }

            },
            Looper.myLooper()


        )
    }
    fun openSms(phone: String, message: String) {

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("smsto:$phone")
            putExtra("sms_body", message)
        }

        startActivity(intent)
    }
    fun openMap(lat: Double, lng: Double) {

        val uri = android.net.Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        startActivity(intent)
    }
}


@Composable
fun LocationDetails(location: Location = Location("") ,  address: String = "No address yet" , onOpenSms: (String, String) -> Unit , onOpenMap : (Double, Double)->Unit) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Current Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                LocationRow("Longitude", location.longitude.toString())
                Spacer(modifier = Modifier.height(12.dp))
                LocationRow("Latitude", location.latitude.toString())
                Spacer(modifier = Modifier.height(24.dp))
                LocationAddress(address)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    val phone = "01010177437"

                    val message = """
                    My Location:
                    Lat: ${location.latitude}
                    Lng: ${location.longitude}
                    Address: $address
                """.trimIndent()

                    onOpenSms(phone, message)
                }){
                    Text(text = "Send SMS")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {

                    onOpenMap(location.latitude , location.longitude)
                }){
                    Text(text = "Open Map")
                }
            }
        }
    }


@Composable
fun LocationRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LocationAddress(address : String = "no address yet") {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "Your Current Address",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = address,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LocationDetailsPreview() {
//   LocationDetails()
}


