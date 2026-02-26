package com.example.kotlin_lab5

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://dummyjson.com/")
        .build()
    val retrofitService : RetrofitService =
        retrofit.create(RetrofitService::class.java)
}