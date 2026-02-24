package com.example.kotlin_lab3task2

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloadService : Service() {

    override fun onBind(intent: Intent): IBinder? {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val url = intent?.getStringExtra("url") ?: return START_NOT_STICKY
        Thread {
            try {
                val uri = downloadImage(url)
                sendBroadcast(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stopSelf()
        }.start()

        return START_NOT_STICKY
    }
    private fun downloadImage(urlString: String): Uri {
        Log.d("Download", "Starting download: $urlString")
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.connect()

        val input = connection.inputStream
        val file = File(cacheDir, "downloaded_image.jpg")
        val output = FileOutputStream(file)

        input.copyTo(output)
        input.close()
        Log.d("Download", "Download complete, file: ${file.absolutePath}")

        return FileProvider.getUriForFile(this, "$packageName.provider", file)
    }

    private fun sendBroadcast(uri: Uri) {
        val intent = Intent("IMAGE_DOWNLOADED").apply {
            putExtra("image_uri", uri.toString())
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }
}