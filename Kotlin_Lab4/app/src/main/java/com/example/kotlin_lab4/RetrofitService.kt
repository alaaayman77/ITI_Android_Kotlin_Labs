package com.example.kotlin_lab4

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {
        @GET("products")
        fun getProducts(): Call<ProductResponse>
}
