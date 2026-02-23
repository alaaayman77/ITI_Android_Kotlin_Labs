package com.example.kotlin_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_lab1.ui.theme.Cake
import com.example.kotlin_lab1.ui.theme.CakesDataSource
import com.example.kotlin_lab1.ui.theme.Kotlin_Lab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin_Lab1Theme {

                        CakesList()
            }
        }
    }
}



@Composable
fun CakeItem(cake: Cake = CakesDataSource.array[0]) {
    Card(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)) {

            Image(
                painter = painterResource(id = cake.image),
                contentDescription = cake.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = cake.title, style = MaterialTheme.typography.titleMedium)
                Text(text = cake.description)
            }
        }
    }
}

@Composable
fun CakesList(cakes: Array<Cake> = CakesDataSource.array) {

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Our Delicious Cakes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(cakes.size) { index ->

            CakeItem(cakes[index])

        }
    }
}
@Preview(showBackground = true)
@Composable
fun CakeItemPreview() {
        CakeItem()
}

@Preview(showBackground = true)
@Composable
fun CakesListPreview() {
        CakesList()
}