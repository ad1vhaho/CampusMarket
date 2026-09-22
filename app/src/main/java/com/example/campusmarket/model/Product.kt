package com.example.campusmarket.model

import java.io.Serializable

/**
 * Represents a product retrieved from the EscuelaJS REST API.
 *
 * The Serializable interface allows a Product object to be
 * passed between Android Activities.
 */
data class Product(
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val category: Category? = null,
    val listingId: String? = null,
    val sellerId: String? = null,
    val sellerName: String? = null,
    val condition: String? = null,
    val location: String? = null
) : Serializable

/**
 * Represents the category associated with a product.
 */
data class Category(
    val id: Int = 0,
    val name: String = "",
    val image: String = ""
) : Serializable
