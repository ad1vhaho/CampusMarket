package com.example.campusmarket.repository

import com.example.campusmarket.model.Conversation
import com.example.campusmarket.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Repository responsible for storing and retrieving
 * CampusMarket conversations and messages from Firestore.
 */
class MessageRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    /**
     * Retrieves conversations belonging to a user.
     *
     * @param userId Firebase user ID.
     * @return list of conversations.
     */
    suspend fun getConversations(
        userId: String
    ): List<Conversation> {

        val snapshot =
            firestore
                .collection("conversations")
                .whereArrayContains(
                    "participantIds",
                    userId
                )
                .get()
                .await()

        return snapshot.documents.mapNotNull { document ->
                document.toObject(Conversation::class.java)?.let { conversation ->
                    val otherId = conversation.participantIds.firstOrNull { it != userId }.orEmpty()
                    conversation.copy(
                        otherUserId = otherId,
                        otherUserName = conversation.participantNames[otherId]
                            ?: conversation.otherUserName.ifBlank { "CampusMarket User" }
                    )
                }
            }
            .sortedByDescending {
                it.updatedAt
            }
    }

    /**
     * Creates a new conversation if one does not already exist.
     *
     * @param currentUserId ID of the logged-in user.
     * @param currentUserName name of the logged-in user.
     * @param otherUserId ID of the other participant.
     * @param otherUserName name of the other participant.
     * @return conversation ID.
     */
    suspend fun getOrCreateConversation(
        currentUserId: String,
        currentUserName: String,
        otherUserId: String,
        otherUserName: String
    ): String {

        val participantIds =
            listOf(
                currentUserId,
                otherUserId
            ).sorted()

        val conversationId =
            participantIds.joinToString("_")

        val conversationReference =
            firestore
                .collection("conversations")
                .document(conversationId)

        val existingConversation =
            conversationReference
                .get()
                .await()

        if (!existingConversation.exists()) {

            val conversation =
                Conversation(
                    id = conversationId,
                    participantIds = participantIds,
                    participantNames = mapOf(
                        currentUserId to currentUserName,
                        otherUserId to otherUserName
                    ),
                    otherUserId = otherUserId,
                    otherUserName = otherUserName,
                    lastMessage = "",
                    updatedAt = System.currentTimeMillis(),
                    unreadCount = 0
                )

            conversationReference
                .set(conversation)
                .await()

            Timber.d(
                "New conversation created: $conversationId"
            )
        }

        return conversationId
    }

    /**
     * Retrieves messages belonging to a conversation.
     *
     * @param conversationId conversation ID.
     * @return messages sorted from oldest to newest.
     */
    suspend fun getMessages(
        conversationId: String
    ): List<Message> {

        val snapshot =
            firestore
                .collection("messages")
                .whereEqualTo(
                    "conversationId",
                    conversationId
                )
                .get()
                .await()

        return snapshot.documents
            .mapNotNull {
                it.toObject(Message::class.java)
            }
            .sortedBy {
                it.timestamp
            }
    }

    /**
     * Sends a message and updates the conversation preview.
     *
     * @param message message to send.
     */
    suspend fun sendMessage(
        message: Message
    ) {

        val messageReference =
            firestore
                .collection("messages")
                .document()

        val messageWithId =
            message.copy(
                id = messageReference.id
            )

        messageReference
            .set(messageWithId)
            .await()

        firestore
            .collection("conversations")
            .document(message.conversationId)
            .update(
                mapOf(
                    "lastMessage" to message.text,
                    "updatedAt" to message.timestamp
                )
            )
            .await()

        Timber.d(
            "Message sent: ${messageReference.id}"
        )
    }
}
