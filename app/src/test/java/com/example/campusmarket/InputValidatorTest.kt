package com.example.campusmarket

import com.example.campusmarket.utils.InputValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidatorTest {
    @Test fun validStudentEmailPasses() = assertTrue(InputValidator.validEmail("student@campus.ac.za"))
    @Test fun emailWithoutAtSignFails() = assertFalse(InputValidator.validEmail("student.campus.ac.za"))
    @Test fun sixCharacterPasswordPasses() = assertTrue(InputValidator.validPassword("abc123"))
    @Test fun shortPasswordFails() = assertFalse(InputValidator.validPassword("12345"))
    @Test fun completeListingPasses() = assertTrue(
        InputValidator.validListing("Textbook", "Books", "Good", "250", "Accounting textbook")
    )
    @Test fun zeroPriceFails() = assertFalse(
        InputValidator.validListing("Textbook", "Books", "Good", "0", "Accounting textbook")
    )
    @Test fun missingDescriptionFails() = assertFalse(
        InputValidator.validListing("Textbook", "Books", "Good", "250", "")
    )
}
