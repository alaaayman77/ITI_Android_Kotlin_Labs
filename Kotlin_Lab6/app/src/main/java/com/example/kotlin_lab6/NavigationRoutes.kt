package com.example.kotlin_lab6

import kotlinx.serialization.Serializable

sealed class NavigationRoutes {
    @Serializable
    object RegisterRoute : NavigationRoutes()
    @Serializable
    data class LoginRoute(val username : String , val password: String) : NavigationRoutes()
    @Serializable
    data class HomeRoute(val username : String ) : NavigationRoutes()
}