package com.example.campusmarket.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusmarket.adapter.ChatAdapter
import com.example.campusmarket.databinding.ActivityChatBinding
import com.example.campusmarket.model.Message
import com.example.campusmarket.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Displays a conversation between the current user
 * and another CampusMarket user.
 */
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var chatAdapter: ChatAdapter

    private val messageRepository =
        MessageRepository()

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    private var conversationId =
        ""

    private var otherUserId =
        ""

    private var otherUserName =
        ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityChatBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        Timber.d("ChatActivity started")

        conversationId =
            intent.getStringExtra(
                EXTRA_CONVERSATION_ID
            ).orEmpty()

        otherUserId =
            intent.getStringExtra(
                EXTRA_OTHER_USER_ID
            ).orEmpty()

        otherUserName =
            intent.getStringExtra(
                EXTRA_OTHER_USER_NAME
            ).orEmpty()

        if (conversationId.isBlank()) {

            Toast.makeText(
                this,
                "Conversation could not be opened.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        binding.tvChatUserName.text =
            otherUserName.ifBlank {
                "CampusMarket Seller"
            }

        setupToolbar()
        setupRecyclerView()
        setupSendButton()
        loadMessages()
    }

    private fun setupToolbar() {

        binding.chatToolbar
            .setNavigationOnClickListener {
                finish()
            }
    }

    private fun setupRecyclerView() {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {
            finish()
            return
        }

        chatAdapter =
            ChatAdapter(
                emptyList(),
                currentUser.uid
            )

        binding.recyclerChat.apply {

            layoutManager =
                LinearLayoutManager(
                    this@ChatActivity
                )

            adapter =
                chatAdapter
        }
    }

    private fun setupSendButton() {

        binding.btnSendMessage.setOnClickListener {

            sendMessage()
        }
    }

    private fun loadMessages() {

        binding.progressChat.visibility =
            View.VISIBLE

        lifecycleScope.launch {

            try {

                val messages =
                    messageRepository
                        .getMessages(
                            conversationId
                        )

                chatAdapter.updateMessages(
                    messages
                )

                if (messages.isNotEmpty()) {

                    binding.recyclerChat
                        .scrollToPosition(
                            messages.lastIndex
                        )
                }

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to load messages"
                )

                Toast.makeText(
                    this@ChatActivity,
                    "Unable to load messages.",
                    Toast.LENGTH_LONG
                ).show()
            }

            binding.progressChat.visibility =
                View.GONE
        }
    }

    private fun sendMessage() {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in before sending a message.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val messageText =
            binding.etMessage
                .text
                ?.toString()
                ?.trim()
                .orEmpty()

        if (messageText.isEmpty()) {

            binding.messageInputLayout.error =
                "Enter a message"

            return
        }

        binding.messageInputLayout.error =
            null

        binding.btnSendMessage.isEnabled =
            false

        lifecycleScope.launch {

            try {

                val message =
                    Message(
                        conversationId =
                            conversationId,
                        senderId =
                            currentUser.uid,
                        senderName =
                            currentUser.email
                                ?: "CampusMarket User",
                        receiverId =
                            otherUserId,
                        text =
                            messageText,
                        timestamp =
                            System.currentTimeMillis()
                    )

                messageRepository
                    .sendMessage(
                        message
                    )

                binding.etMessage.text?.clear()

                loadMessages()

                Timber.d(
                    "Message sent successfully"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to send message"
                )

                Toast.makeText(
                    this@ChatActivity,
                    "Unable to send message.",
                    Toast.LENGTH_LONG
                ).show()
            }

            binding.btnSendMessage.isEnabled =
                true
        }
    }

    companion object {

        const val EXTRA_CONVERSATION_ID =
            "extra_conversation_id"

        const val EXTRA_OTHER_USER_ID =
            "extra_other_user_id"

        const val EXTRA_OTHER_USER_NAME =
            "extra_other_user_name"
    }
}