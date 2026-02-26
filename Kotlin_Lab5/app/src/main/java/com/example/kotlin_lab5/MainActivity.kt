package com.example.kotlin_lab5

import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.kotlin_lab5.ui.theme.Kotlin_Lab5Theme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val dao by lazy { db.productDao() }

    private val products = mutableStateOf<List<Product>>(emptyList())
    private val isLoading = mutableStateOf(true)
    private val isOffline = mutableStateOf(false)
    private val errorMsg = mutableStateOf<String?>(null)
    private val selectedProduct = mutableStateOf<Product?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadProducts()

        setContent {
            Kotlin_Lab5Theme {
                ProductsApp()
            }
        }
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            isLoading.value = true
            errorMsg.value = null

            if (isConnected()) {
                isOffline.value = false
                try {
                    val response = RetrofitHelper.retrofitService.getProducts()
                    val entities = (response.body()?.products ?: listOf()).map {
                        Product(it.id, it.title, it.price, it.description, it.thumbnail)
                    }
                    dao.insertAll(entities)
                } catch (e: Exception) {
                    errorMsg.value = e.message ?: "Failed to fetch products"
                }
            } else {
                isOffline.value = true
            }

            val cached = dao.getAllProducts()
            if (cached.isNotEmpty()) {
                products.value = cached
                errorMsg.value = null
            } else if (errorMsg.value == null) {
                errorMsg.value = "No internet connection and no cached data"
            }

            isLoading.value = false
        }
    }

    private fun openDetail(product: Product) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id", product.id)
        intent.putExtra("title", product.title)
        intent.putExtra("price", product.price)
        intent.putExtra("description", product.description)
        intent.putExtra("thumbnail", product.thumbnail)
        startActivity(intent)
    }

    @Composable
    fun SimpleTopBar(title: String) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    @Composable
    fun OfflineBanner() {
        if (isOffline.value) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(8.dp))
                Text("Offline – showing cached data", style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    @Composable
    fun ProductsApp() {
        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        when {
            isLoading.value -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMsg.value != null -> {
                Box(
                    Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${errorMsg.value}", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { loadProducts() }) { Text("Retry") }
                    }
                }
            }

            isLandscape -> {
                LandscapeLayout()
            }

            else -> {
                PortraitListScreen()
            }
        }
    }

    @Composable
    fun PortraitListScreen() {
        Column(Modifier.fillMaxSize()) {
            SimpleTopBar(title = "Products")
            OfflineBanner()
            LazyColumn {
                items(products.value) { product ->
                    ProductRow(
                        product = product,
                        isSelected = false,
                        onClick = { openDetail(product) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    @Composable
    fun LandscapeLayout() {
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f)) {
                SimpleTopBar(title = "Products")
                OfflineBanner()
                LazyColumn {
                    items(products.value) { product ->
                        ProductRow(
                            product = product,
                            isSelected = product.id == selectedProduct.value?.id,
                            onClick = { selectedProduct.value = product }
                        )
                        HorizontalDivider()
                    }
                }
            }

            Box(
                Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedProduct.value != null) {
                    DetailContent(selectedProduct.value!!)
                } else {
                    Text("Select a product", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ProductRow(
        product: Product,
        isSelected: Boolean = false,
        onClick: () -> Unit
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Text(
                product.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun DetailContent(product: Product) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            GlideImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
            Text(
                product.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "$${product.price}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text(product.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}