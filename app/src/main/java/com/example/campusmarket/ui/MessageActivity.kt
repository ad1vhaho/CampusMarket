package com.example.campusmarket.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusmarket.adapter.ConversationAdapter
import com.example.campusmarket.databinding.ActivityMessagesBinding
import com.example.campusmarket.model.Conversation
import com.example.campusmarket.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Displays conversations belonging to the currently
 * authenticated CampusMarket user.
 */
class MessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesBinding

    private lateinit var conversationAdapter: ConversationAdapter

    private val messageRepository =
        MessageRepository()

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityMessagesBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        Timber.d("MessagesActivity started")

        setupToolbar()
        setupRecyclerView()
        loadConversations()
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            loadConversations()
        }
    }

    private fun setupToolbar() {
        binding.messagesToolbar
            .setNavigationOnClickListener {
                finish()
            }
    }

    private fun setupRecyclerView() {

        conversationAdapter =
            ConversationAdapter(
                emptyList()
            ) { conversation ->

                openConversation(
                    conversation
                )
            }

        binding.recyclerMessages.apply {

            layoutManager =
                LinearLayoutManager(
                    this@MessagesActivity
                )

            adapter =
                conversationAdapter
        }
    }

    private fun loadConversations() {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in to view your messages.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        binding.progressMessages.visibility =
            View.VISIBLE

        lifecycleScope.launch {

            try {

                val conversations =
                    messageRepository
                        .getConversations(
                            currentUser.uid
                        )

                conversationAdapter
                    .updateConversations(
                        conversations
                    )

                updateEmptyState(
                    conversations
                )

                Timber.d(
                    "Loaded ${conversations.size} conversations"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to load conversations"
                )

                Toast.makeText(
                    this@MessagesActivity,
                    "Unable to load messages.",
                    Toast.LENGTH_LONG
                ).show()
            }

            binding.progressMessages.visibility =
                View.GONE
        }
    }

    private fun updateEmptyState(
        conversations: List<Conversation>
    ) {

        if (conversations.isEmpty()) {

            binding.tvEmptyMessages.visibility =
                View.VISIBLE

            binding.recyclerMessages.visibility =
                View.GONE

        } else {

            binding.tvEmptyMessages.visibility =
                View.GONE

            binding.recyclerMessages.visibility =
                View.VISIBLE
        }
    }

    private fun openConversation(
        conversation: Conversation
    ) {

        val intent =
            Intent(
                this,
                ChatActivity::class.java
            )

        intent.putExtra(
            ChatActivity.EXTRA_CONVERSATION_ID,
            conversation.id
        )

        intent.putExtra(
            ChatActivity.EXTRA_OTHER_USER_ID,
            conversation.otherUserId
        )

        intent.putExtra(
            ChatActivity.EXTRA_OTHER_USER_NAME,
            conversation.otherUserName
        )

        startActivity(intent)
    }
}