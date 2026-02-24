package com.example.kotlin_lab3

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAndTimeService : Service() {
    private val myBinder: IBinder = MyLocalBinder()
    override fun onBind(intent: Intent): IBinder {
       return myBinder
    }
    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US)
        return dateFormat.format(Date())
    }
    inner class MyLocalBinder : Binder() {
        fun getService(): DateAndTimeService {
            return this@DateAndTimeService
        }
    }
}