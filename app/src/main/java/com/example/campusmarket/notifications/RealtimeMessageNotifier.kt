package com.example.campusmarket.notifications

import android.content.Context
import com.example.campusmarket.R
import com.example.campusmarket.utils.SettingsDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Creates immediate device notifications when a new Firestore message arrives. */
class RealtimeMessageNotifier(private val context: Context) {
    private var registration: ListenerRegistration? = null
    private var listeningSince = 0L
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start(userId: String) {
        stop()
        listeningSince = System.currentTimeMillis()
        registration = FirebaseFirestore.getInstance().collection("messages")
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documentChanges?.forEach { change ->
                    val timestamp = change.document.getLong("timestamp") ?: 0L
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED && timestamp > listeningSince) {
                        val sender = change.document.getString("senderName")
                            ?: context.getString(R.string.campus_market_user)
                        val text = change.document.getString("text").orEmpty()
                        scope.launch {
                            if (SettingsDataStore(context).notificationsEnabled.first()) {
                                NotificationHelper.showMessage(
                                    context,
                                    context.getString(R.string.new_message_from, sender),
                                    text,
                                    change.document.id.hashCode()
                                )
                            }
                        }
                    }
                }
            }
    }

    fun stop() {
        registration?.remove()
        registration = null
    }
}
