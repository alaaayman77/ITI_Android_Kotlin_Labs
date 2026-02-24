package com.example.kotlin_lab3task2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri


class ImageReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val uriString = intent?.getStringExtra("image_uri") ?: return
        val uri = Uri.parse(uriString)

        val activityIntent = Intent(context, DisplayActivity::class.java).apply {
            putExtra("image_uri", uriString)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data = uri
        }
        context?.startActivity(activityIntent)
    }
}