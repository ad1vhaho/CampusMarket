package com.example.campusmarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.campusmarket.model.Product
import com.example.campusmarket.repository.ProductRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the CampusMarket Home screen.
 *
 * The ViewModel retrieves products from the repository and
 * exposes the result to the Activity.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    private val _products =
        MutableLiveData<List<Product>>()

    /**
     * Products retrieved from the REST API.
     */
    val products: LiveData<List<Product>> =
        _products

    private val _error =
        MutableLiveData<String?>()

    /**
     * Error message produced when product loading fails.
     */
    val error: LiveData<String?> =
        _error

    private val _usingOfflineCache = MutableLiveData(false)
    val usingOfflineCache: LiveData<Boolean> = _usingOfflineCache

    /**
     * Loads products from the REST API.
     */
    fun loadProducts() {

        viewModelScope.launch {

            try {

                Timber.d("Requesting products from EscuelaJS API")

                val result =
                    repository.getProducts()

                _products.value = result.products
                _usingOfflineCache.value = result.fromCache
                _error.value = null

                Timber.d(
                    "Catalogue response received: ${result.products.size} products; cache=${result.fromCache}"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Unable to retrieve products"
                )

                _error.value =
                    "Unable to load products. Please try again."
            }
        }
    }
}
