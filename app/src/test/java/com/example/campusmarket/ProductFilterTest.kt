package com.example.campusmarket

import com.example.campusmarket.model.Category
import com.example.campusmarket.model.Product
import com.example.campusmarket.utils.ProductFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFilterTest {
    private val products = listOf(
        Product(1, "Accounting textbook", 250.0, "First year book", category = Category(1, "Books")),
        Product(2, "Laptop", 5000.0, "Student computer", category = Category(2, "Electronics")),
        Product(3, "Winter jacket", 450.0, "Warm clothing", category = Category(3, "Clothing"))
    )

    @Test fun searchMatchesTitleAndDescriptionIgnoringCase() {
        assertEquals(listOf(2), ProductFilter.filter(products, "COMPUTER", "All").map { it.id })
    }

    @Test fun categoryAndSearchAreCombined() {
        assertEquals(listOf(3), ProductFilter.filter(products, "jacket", "Clothing").map { it.id })
    }

    @Test fun allWithBlankSearchReturnsEverything() {
        assertEquals(3, ProductFilter.filter(products, "", "All").size)
    }
}
