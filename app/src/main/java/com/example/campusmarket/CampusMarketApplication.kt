package com.example.campusmarket

import android.app.Application
import com.example.campusmarket.notifications.NotificationHelper
import com.example.campusmarket.notifications.RealtimeMessageNotifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import timber.log.Timber

/** Application-wide setup for logging, offline Firestore data and notifications. */
class CampusMarketApplication : Application() {
    private lateinit var realtimeMessageNotifier: RealtimeMessageNotifier

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

        NotificationHelper.createChannel(this)
        realtimeMessageNotifier = RealtimeMessageNotifier(this)
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) realtimeMessageNotifier.stop() else realtimeMessageNotifier.start(user.uid)
        }
    }
}
