package com.example.campusmarket.model

/**
 * Represents a CampusMarket listing stored in Firebase Firestore.
 */
data class Listing(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val title: String = "",
    val category: String = "",
    val condition: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val location: String = "Hatfield",
    val status: String = "Active",
    val createdAt: Long = System.currentTimeMillis()
)