package com.example.campusmarket.utils

import com.example.campusmarket.model.Product

object ProductFilter {
    fun filter(products: List<Product>, query: String, category: String): List<Product> {
        val needle = query.trim().lowercase()
        return products.filter { product ->
            val matchesText = needle.isEmpty() || product.title.contains(needle, true) ||
                product.description.contains(needle, true) || (product.category?.name?.contains(needle, true) == true)
            val matchesCategory = category == "All" || when (category) {
                "Books" -> product.category?.name?.contains("book", true) == true ||
                    listOf("book", "textbook", "novel").any { product.title.contains(it, true) }
                else -> product.category?.name?.equals(category, true) == true
            }
            matchesText && matchesCategory
        }
    }
}
