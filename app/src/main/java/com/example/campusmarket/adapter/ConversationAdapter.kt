package com.example.campusmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ItemConversationBinding
import com.example.campusmarket.model.Conversation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RecyclerView adapter used to display CampusMarket conversations.
 */
class ConversationAdapter(
    private var conversations: List<Conversation>,
    private val onConversationClicked: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    /**
     * Holds the views for one conversation item.
     */
    class ConversationViewHolder(
        val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationViewHolder {

        val binding =
            ItemConversationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ConversationViewHolder,
        position: Int
    ) {
        val conversation =
            conversations[position]

        holder.binding.tvConversationName.text =
            conversation.otherUserName

        holder.binding.tvConversationPreview.text =
            conversation.lastMessage.ifBlank {
                "Start a conversation"
            }

        holder.binding.tvConversationTime.text =
            formatTime(conversation.updatedAt)

        holder.binding.ivConversationAvatar.setImageResource(
            R.drawable.ic_campus_market_logo
        )

        holder.binding.viewUnread.visibility =
            if (conversation.unreadCount > 0) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

        holder.itemView.setOnClickListener {
            onConversationClicked(conversation)
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    /**
     * Updates the adapter with a new list of conversations.
     */
    fun updateConversations(
        newConversations: List<Conversation>
    ) {
        conversations = newConversations
        notifyDataSetChanged()
    }

    private fun formatTime(
        timestamp: Long
    ): String {

        if (timestamp <= 0) {
            return ""
        }

        return SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(
            Date(timestamp)
        )
    }
}