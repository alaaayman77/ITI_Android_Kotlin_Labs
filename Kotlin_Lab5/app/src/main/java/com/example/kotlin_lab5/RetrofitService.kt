package com.example.kotlin_lab5

import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService {
    @GET("products")
    suspend fun getProducts(): Response<ProductResponse>
}