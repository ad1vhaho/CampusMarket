package com.example.campusmarket.model

/**
 * Represents a single message inside a CampusMarket conversation.
 */
data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)