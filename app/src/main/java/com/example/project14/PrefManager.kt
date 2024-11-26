package com.example.project14

import android.content.Context

class PrefManager private constructor(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) // Mengakses SharedPreferences untuk menyimpan data
    private val editor = sharedPreferences.edit() // Menginisialisasi editor untuk mengubah data

    companion object {
        private const val PREF_NAME = "user_pref" // Nama file SharedPreferences
        private var instance: PrefManager? = null // Instance singleton

        // Fungsi untuk mendapatkan instance PrefManager (Singleton)
        fun getInstance(context: Context): PrefManager {
            if (instance == null) {
                instance = PrefManager(context.applicationContext) // Membuat instance jika belum ada
            }
            return instance!! // Mengembalikan instance PrefManager
        }
    }

    // Fungsi untuk menyimpan username ke SharedPreferences
    fun saveUsername(username: String) {
        editor.putString("USERNAME", username).apply() // Menyimpan username
    }

    // Fungsi untuk mendapatkan username dari SharedPreferences
    fun getUsername(): String {
        return sharedPreferences.getString("USERNAME", "") ?: "" // Mengambil username, default jika tidak ada
    }

    // Fungsi untuk menghapus username dari SharedPreferences
    fun clearUsername() {
        editor.remove("USERNAME").apply() // Menghapus username yang disimpan
    }

    // Fungsi untuk menghapus semua data sesi
    fun clearAll() {
        editor.clear().apply() // Menghapus semua data yang disimpan
    }
}
