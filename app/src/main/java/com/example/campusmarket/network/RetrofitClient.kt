package com.example.campusmarket.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides the Retrofit instance used to communicate with
 * the EscuelaJS REST API.
 */
object RetrofitClient {

    private const val BASE_URL =
        "https://api.escuelajs.co/api/v1/"

    /**
     * Retrofit service used throughout the application.
     */
    val apiService: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}