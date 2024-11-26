package com.example.project14

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.project14.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var prefManager: PrefManager
    private val channelID = "TES_NOTIF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Instansiasi PrefManager untuk menyimpan dan mengambil data sesi pengguna
        prefManager = PrefManager.getInstance(this)
        checkLoginStatus()

        // Membuat NotificationChannel untuk Android 8 dan lebih tinggi
        createNotificationChannel()

        // Memeriksa izin notifikasi (Android 13 ke atas)
        checkNotificationPermission()

        with(binding) {
            // Tombol login untuk memverifikasi username dan password
            btnLogin.setOnClickListener {
                val usernameUntukLogin = "karina"
                val passwordUntukLogin = "12345"
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()

                // Cek apakah username dan password sudah sesuai
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Username dan password harus diisi",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (username == usernameUntukLogin && password == passwordUntukLogin) {
                    prefManager.saveUsername(username)  // Simpan username jika login berhasil
                    checkLoginStatus()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Username atau password salah",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Tombol logout untuk menghapus data sesi pengguna
            btnLogout.setOnClickListener {
                prefManager.clearUsername()  // Hapus username yang tersimpan
                checkLoginStatus()  // Perbarui tampilan status login
            }

            // Tombol clear untuk menghapus data pengguna dan restart aplikasi
            btnClear.setOnClickListener {
                clearDataAndRestart()
            }

            // Tombol untuk mengirimkan notifikasi
            btnNotif.setOnClickListener {
                sendNotification()
            }
        }
    }

    // Memeriksa status login pengguna dan memperbarui tampilan
    private fun checkLoginStatus() {
        val isLoggedIn = prefManager.getUsername()
        if (isLoggedIn.isEmpty()) {
            // Jika tidak login, tampilkan form login
            binding.txtLogin.visibility = View.VISIBLE
            binding.txtLog.visibility = View.GONE
        } else {
            // Jika login, tampilkan username dan status login
            binding.txtLogin.visibility = View.GONE
            binding.txtLog.visibility = View.VISIBLE
            binding.txtUsername.text = "Hello, $isLoggedIn"
        }
    }

    // Fungsi untuk menghapus data pengguna dan me-restart aplikasi
    private fun clearDataAndRestart() {
        prefManager.clearUsername()  // Hapus data pengguna
        Toast.makeText(this, "Data berhasil dihapus. Harap login kembali.", Toast.LENGTH_SHORT).show()

        // Memulai ulang aktivitas untuk membersihkan tampilan
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()  // Menutup aktivitas saat ini
    }

    // Membuat NotificationChannel untuk Android 8 dan lebih tinggi
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                "Notif PPPB",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Memeriksa izin notifikasi pada perangkat Android 13 ke atas
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    // Mengatasi hasil permintaan izin notifikasi
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Izin notifikasi diberikan.")
            } else {
                Toast.makeText(this, "Izin notifikasi diperlukan untuk fitur ini.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mengirim notifikasi dengan tombol aksi (Baca Notif dan Logout)
    private fun sendNotification() {
        // Memeriksa apakah izin notifikasi telah diberikan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin notifikasi diperlukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }

        // Intent untuk tombol "Baca Notif"
        val intentBacaNotif = Intent(this, NotifReceiver::class.java)
            .putExtra("MESSAGE", "Baca selengkapnya ...")
        val pendingIntentBacaNotif = PendingIntent.getBroadcast(
            this,
            0,
            intentBacaNotif,
            flag
        )

        // Intent untuk tombol "Logout"
        val intentLogout = Intent(this, LogoutReceiver::class.java)
        val pendingIntentLogout = PendingIntent.getBroadcast(
            this,
            1,
            intentLogout,
            flag
        )

        // Membangun dan mengirim notifikasi dengan dua tombol aksi
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Notifikasi PPPB")
            .setContentText("Hello from the other side")
            .setAutoCancel(true)
            .addAction(0, "Baca Notif", pendingIntentBacaNotif)
            .addAction(0, "Logout", pendingIntentLogout)

        // Mengirim notifikasi
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}
