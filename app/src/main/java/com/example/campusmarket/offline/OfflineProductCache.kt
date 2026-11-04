package com.example.campusmarket.offline

import android.content.Context
import com.example.campusmarket.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/** Stores the latest successful REST response so the catalogue remains available offline. */
class OfflineProductCache(context: Context) {
    private val preferences = context.getSharedPreferences("offline_catalogue", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun save(products: List<Product>, savedAt: Long = System.currentTimeMillis()) {
        preferences.edit()
            .putString(KEY_PRODUCTS, gson.toJson(products))
            .putLong(KEY_SAVED_AT, savedAt)
            .apply()
    }

    fun load(): CachedProducts {
        val json = preferences.getString(KEY_PRODUCTS, null) ?: return CachedProducts(emptyList(), 0L)
        val type = object : TypeToken<List<Product>>() {}.type
        return runCatching {
            CachedProducts(gson.fromJson<List<Product>>(json, type).orEmpty(), preferences.getLong(KEY_SAVED_AT, 0L))
        }.getOrDefault(CachedProducts(emptyList(), 0L))
    }

    data class CachedProducts(val products: List<Product>, val savedAt: Long)

    private companion object {
        const val KEY_PRODUCTS = "products"
        const val KEY_SAVED_AT = "saved_at"
    }
}
