package com.example.campusmarket.model

/**
 * Represents a conversation between a CampusMarket user
 * and another user or seller.
 */
data class Conversation(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val otherUserId: String = "",
    val otherUserName: String = "",
    val lastMessage: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)
