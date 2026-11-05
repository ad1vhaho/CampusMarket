package com.example.campusmarket.repository

import android.content.Context
import com.example.campusmarket.model.Product
import com.example.campusmarket.network.RetrofitClient
import com.example.campusmarket.offline.OfflineProductCache
import com.example.campusmarket.offline.OfflineModePolicy

/**
 * Repository responsible for retrieving product data.
 *
 * The repository separates the data source from the ViewModel.
 */
class ProductRepository(context: Context) {
    private val cache = OfflineProductCache(context)

    /**
     * Retrieves products from the EscuelaJS API.
     *
     * @return list of products.
     */
    suspend fun getProducts(): ProductLoadResult {
        return try {
            val products = RetrofitClient.apiService.getProducts()
            cache.save(products)
            ProductLoadResult(products, fromCache = false)
        } catch (exception: Exception) {
            val cached = cache.load()
            if (!OfflineModePolicy.canUseCache(networkRequestFailed = true, cachedItemCount = cached.products.size)) throw exception
            ProductLoadResult(cached.products, fromCache = true, cachedAt = cached.savedAt)
        }
    }
}

data class ProductLoadResult(
    val products: List<Product>,
    val fromCache: Boolean,
    val cachedAt: Long = 0L
)
