package com.example.campusmarket.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/** Receives Firebase Cloud Messaging notifications in foreground and background. */
class CampusMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "CampusMarket"
        val body = message.notification?.body ?: message.data["body"] ?: "You have a new update."
        NotificationHelper.showMessage(this, title, body, body.hashCode())
    }

    override fun onNewToken(token: String) {
        Timber.d("FCM registration token refreshed")
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
        }
    }
}
