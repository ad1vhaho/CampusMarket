package com.example.campusmarket.utils

/** Languages supplied as complete Part 3 Android resources. */
enum class SupportedLanguage(val tag: String) {
    ENGLISH("en"),
    TSHIVENDA("ve"),
    AFRIKAANS("af");

    companion object {
        fun fromTag(tag: String): SupportedLanguage = entries.firstOrNull { it.tag == tag } ?: ENGLISH
    }
}
