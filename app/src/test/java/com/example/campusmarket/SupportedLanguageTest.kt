package com.example.campusmarket

import com.example.campusmarket.utils.SupportedLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class SupportedLanguageTest {
    @Test fun `recognises Tshivenda locale`() {
        assertEquals(SupportedLanguage.TSHIVENDA, SupportedLanguage.fromTag("ve"))
    }

    @Test fun `unknown locale safely falls back to English`() {
        assertEquals(SupportedLanguage.ENGLISH, SupportedLanguage.fromTag("xx"))
    }
}
