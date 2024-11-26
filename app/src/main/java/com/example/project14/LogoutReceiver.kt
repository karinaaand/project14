package com.example.project14

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class LogoutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Mendapatkan instance PrefManager untuk mengakses SharedPreferences
        val prefManager = PrefManager.getInstance(context)

        // Menghapus data sesi pengguna dari SharedPreferences
        // clearAll() akan menghapus data yang disimpan, seperti username, password, dll.
        prefManager.clearAll()

        // Menampilkan pesan Toast untuk memberitahukan pengguna bahwa logout berhasil
        Toast.makeText(context, "Logout berhasil. Data sesi telah dihapus.", Toast.LENGTH_SHORT).show()

        // Restart aplikasi dan arahkan ke MainActivity untuk memastikan tampilan login kembali muncul
        val restartIntent = Intent(context, MainActivity::class.java)
        restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(restartIntent)
    }
}
