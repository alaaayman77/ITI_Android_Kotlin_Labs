package com.example.kotlin_lab4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson

class FetchProductsWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    override fun doWork(): Result {
        return try {

            val response = RetrofitClient.api.getProducts().execute()

            if (response.isSuccessful) {
                val products = response.body()?.products ?: emptyList()
                val json = Gson().toJson(products)
                Result.success(workDataOf("products_json" to json))
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}