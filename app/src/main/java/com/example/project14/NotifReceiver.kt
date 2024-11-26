package com.example.project14

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotifReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Mengambil pesan yang dikirim melalui intent
        val msg = intent?.getStringExtra("MESSAGE")

        // Jika pesan tidak null, tampilkan dalam bentuk Toast
        if (msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
