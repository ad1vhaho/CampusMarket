package com.example.campusmarket.network

import com.example.campusmarket.model.Product
import retrofit2.http.GET

/**
 * Defines the REST API endpoints used by CampusMarket.
 */
interface ApiService {

    /**
     * Retrieves products from the EscuelaJS REST API.
     *
     * @return a list of products.
     */
    @GET("products")
    suspend fun getProducts(): List<Product>
}