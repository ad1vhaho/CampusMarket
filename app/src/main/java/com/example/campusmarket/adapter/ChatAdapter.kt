package com.example.campusmarket.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ItemChatMessageBinding
import com.example.campusmarket.model.Message

/**
 * RecyclerView adapter used to display messages in a conversation.
 */
class ChatAdapter(
    private var messages: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    /**
     * Holds the views for one chat message.
     */
    class ChatViewHolder(
        val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {

        val binding =
            ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {

        val message =
            messages[position]

        holder.binding.tvChatMessage.text =
            message.text

        val layoutParams =
            holder.binding.tvChatMessage
                .layoutParams as LinearLayout.LayoutParams

        if (message.senderId == currentUserId) {

            layoutParams.gravity =
                Gravity.END

            holder.binding.tvChatMessage.setBackgroundResource(
                R.drawable.bg_message_sent
            )

            holder.binding.tvChatMessage.setTextColor(
                Color.WHITE
            )

        } else {

            layoutParams.gravity =
                Gravity.START

            holder.binding.tvChatMessage.setBackgroundResource(
                R.drawable.bg_message_received
            )

            holder.binding.tvChatMessage.setTextColor(
                Color.parseColor("#111111")
            )
        }

        holder.binding.tvChatMessage.layoutParams =
            layoutParams
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    /**
     * Updates the displayed messages.
     */
    fun updateMessages(
        newMessages: List<Message>
    ) {
        messages = newMessages
        notifyDataSetChanged()
    }
}