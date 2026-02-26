package com.example.kotlin_lab6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.kotlin_lab6.ui.theme.Kotlin_Lab6Theme

class MainActivity : ComponentActivity() {
    private lateinit var navController : NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            NavHost(
                navController = navController ,
                startDestination = NavigationRoutes.RegisterRoute
            ){
                composable<NavigationRoutes.RegisterRoute> {
                    RegisterScreen(){username , password ->
                        navController.navigate(
                            NavigationRoutes.LoginRoute(username , password)
                        )
                    }
                }
                composable<NavigationRoutes.LoginRoute> {it->
                    val username = it.toRoute<NavigationRoutes.LoginRoute>().username
                    val password = it.toRoute<NavigationRoutes.LoginRoute>().password
                    LoginScreen( username , password)
                    {
                        username->
                        navController.navigate(NavigationRoutes.HomeRoute(username))
                    }
                }
                composable<NavigationRoutes.HomeRoute> {it->
                    val username = it.toRoute<NavigationRoutes.HomeRoute>().username
                    HomeScreen( username){
                        navController.popBackStack(
                            NavigationRoutes.RegisterRoute,
                            inclusive = false
                        )
                    }
                }
            }
        }
    }
}

