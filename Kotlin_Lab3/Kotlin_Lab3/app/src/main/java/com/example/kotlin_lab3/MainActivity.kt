package com.example.kotlin_lab3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_lab3.ui.theme.Kotlin_Lab3Theme

class MainActivity : ComponentActivity() {

    private val myServiceState = mutableStateOf<DateAndTimeService?>(null)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DateAndTimeService.MyLocalBinder
            myServiceState.value = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myServiceState.value = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = Intent(this, DateAndTimeService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            LocalBoundScreen(myService = myServiceState.value)
        }
    }
}
@Composable
fun LocalBoundScreen(myService: DateAndTimeService?) {
    var currentTime = remember { mutableStateOf("Press the button to get time") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = currentTime.value, fontSize = 20.sp, modifier = Modifier.padding(8.dp))

        Button(onClick = {
            myService?.let {
                currentTime.value = it.getCurrentTime()
            }
        }) {
            Text("Get Current Time")
        }
    }
}

