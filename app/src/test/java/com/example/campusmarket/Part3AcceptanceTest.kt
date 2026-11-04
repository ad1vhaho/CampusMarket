package com.example.campusmarket

import com.example.campusmarket.model.Category
import com.example.campusmarket.model.Product
import com.example.campusmarket.offline.OfflineModePolicy
import com.example.campusmarket.utils.InputValidator
import com.example.campusmarket.utils.ProductFilter
import com.example.campusmarket.utils.SupportedLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Part3AcceptanceTest {
    @Test
    fun passwordMinimumLength() {
        // Accept the minimum length and reject one character fewer.
        assertTrue(InputValidator.validPassword("abc123"))
        assertFalse(InputValidator.validPassword("abc12"))
    }

    @Test
    fun listingPrice() {
        // Accept a positive price and reject nonpositive, invalid and non-finite amounts.
        assertTrue(InputValidator.validPrice("250.50"))
        listOf("0", "-5", "abc", "NaN", "Infinity").forEach {
            assertFalse(InputValidator.validPrice(it))
        }
    }

    @Test
    fun catalogueSearch() {
        // Match a description ignoring case and return no results for an unmatched search.
        val catalogue = listOf(Product(1, "Textbook", 250.0, "Accounting", category = Category(1, "Books")))
        assertEquals(listOf(1), ProductFilter.filter(catalogue, "ACCOUNTING", "Books").map { it.id })
        assertTrue(ProductFilter.filter(catalogue, "unmatched", "Books").isEmpty())
    }

    @Test
    fun offlineCatalogue() {
        // Use a populated cache after a failed request and reject an empty cache.
        assertTrue(OfflineModePolicy.canUseCache(networkRequestFailed = true, cachedItemCount = 1))
        assertFalse(OfflineModePolicy.canUseCache(networkRequestFailed = true, cachedItemCount = 0))
    }

    @Test
    fun languageSelection() {
        // Resolve both extra locales and fall back to English for an unsupported tag.
        assertEquals(SupportedLanguage.TSHIVENDA, SupportedLanguage.fromTag("ve"))
        assertEquals(SupportedLanguage.AFRIKAANS, SupportedLanguage.fromTag("af"))
        assertEquals(SupportedLanguage.ENGLISH, SupportedLanguage.fromTag("xx"))
    }
}
