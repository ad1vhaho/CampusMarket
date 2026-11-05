package com.example.campusmarket.utils

object InputValidator {
    fun validEmail(email: String): Boolean =
        email.trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))

    fun validPassword(password: String): Boolean = password.length >= 6

    fun validPrice(price: String): Boolean {
        val amount = price.toDoubleOrNull() ?: return false
        return amount.isFinite() && amount > 0.0
    }

    fun validListing(
        title: String,
        category: String,
        condition: String,
        price: String,
        description: String
    ): Boolean = title.isNotBlank() && category.isNotBlank() && condition.isNotBlank() &&
        validPrice(price) && description.isNotBlank()
}
