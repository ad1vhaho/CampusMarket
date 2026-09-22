package com.example.campusmarket.repository

import com.example.campusmarket.model.Product
import com.example.campusmarket.network.RetrofitClient

/**
 * Repository responsible for retrieving product data.
 *
 * The repository separates the data source from the ViewModel.
 */
class ProductRepository {

    /**
     * Retrieves products from the EscuelaJS API.
     *
     * @return list of products.
     */
    suspend fun getProducts(): List<Product> {

        return RetrofitClient
            .apiService
            .getProducts()
    }
}