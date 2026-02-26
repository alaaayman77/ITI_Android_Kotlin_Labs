package com.example.kotlin_lab6

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(

    registeredUser: String,
    registeredPass: String,
    navigate : (String)->Unit
) {

    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var error = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(error.value, color = Color.Red)

        Button(
            onClick = {

                if (username.value == registeredUser &&
                    password.value == registeredPass
                ) {
                    navigate.invoke(username.value)


                } else {
                    error.value = "Invalid credentials"
                }
            }
        ) {
            Text("Login")
        }
    }
}