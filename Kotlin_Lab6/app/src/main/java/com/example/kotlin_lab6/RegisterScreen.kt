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
fun RegisterScreen(navigate : (String , String)->Unit) {

    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var confirm = remember { mutableStateOf("") }
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

        OutlinedTextField(
            value = confirm.value,
            onValueChange = { confirm.value = it },
            label = { Text("Confirm Password") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(error.value, color = Color.Red)

        Button(onClick = {
            when {
                username.value.isEmpty() ||
                        password.value.isEmpty() ||
                        confirm.value.isEmpty() -> {
                    error.value = "All fields required"
                }

                password.value != confirm.value -> {
                    error.value = "Passwords do not match"
                }

                else -> {
                    error.value = ""
                    navigate.invoke(username.value , password.value)

                }
            }
        }){
            Text("Sign up")
        }

    }
}
