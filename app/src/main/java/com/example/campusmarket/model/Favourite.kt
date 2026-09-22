package com.example.campusmarket.model

/**
 * Represents a product saved by a CampusMarket user
 * as a favourite.
 */
data class Favourite(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val condition: String = "",
    val createdAt: Long = System.currentTimeMillis()
)