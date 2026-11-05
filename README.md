# CampusMarket

CampusMarket is a native Kotlin Android marketplace where students can discover products, publish listings, save favourites and communicate with sellers. Version 2.0 is the final Part 3 release and adds Google single sign-on, offline catalogue access and Firestore synchronisation, real-time notifications, and English, Tshivenda and Afrikaans language support.

## Group members and responsibilities

| Member | Main Part 3 work |
| --- | --- |
| Adivhaho Mphaphuli | Google Sign-In, offline catalogue, Firestore synchronisation and offline tests |
| Dineo Makgatho | Notifications, English, Tshivenda and Afrikaans language support, and related tests |
| Both members | Existing marketplace features, interface checks, phone testing, documentation and the final video |

We divided the new features between us and reviewed the completed app together. Our working branches are `feature/google-offline` and `feature/notifications-language`.

## Part 3 features

- Firebase email/password authentication and Google Sign-In
- REST catalogue supplied by the EscuelaJS/Platzi API through Retrofit
- Locally cached REST catalogue for offline browsing
- Firestore offline persistence and automatic pending-write synchronisation
- Visible offline and reconnection/synchronisation status
- Firebase Cloud Messaging registration, topic subscription and token storage
- Real-time local notifications when a new Firestore message arrives
- English, Tshivenda and Afrikaans Android resources with a persistent language selector
- User-created active and draft listings
- Search and category filtering
- Persistent favourites
- Firestore conversations and chat messages
- Profile editing, password change, dark mode and support screens
- Firebase security rules, JUnit tests and GitHub Actions

## Architecture and services

The app uses Android Activities and View Binding for its screens. ViewModels hold screen data, while repositories handle the REST API and Firestore. Firebase Authentication manages user accounts. Retrofit connects the app to the external product API. The app saves the latest successful product response so that users can still browse products if their connection drops. Firestore also keeps local data and sends pending changes when the device reconnects.

Firebase Cloud Messaging is used for remote notifications. The app also listens for new Firestore messages and shows a notification when a signed-in user receives a chat message.

## Firebase setup

1. Open the existing `campusmarket-108de` Firebase project.
2. Enable Email/Password and Google under Authentication > Sign-in method.
3. Run `./gradlew signingReport` or the Android Studio `signingReport` Gradle task.
4. Add the debug SHA-1 and SHA-256 fingerprints to the Android app in Firebase Project Settings.
5. Add the release certificate fingerprints before generating the final release.
6. Download the refreshed `google-services.json` and replace `app/google-services.json`.
7. Publish `firestore.rules` from the Firebase console or Firebase CLI.
8. Run the app, allow notifications, and verify that the signed-in user's `users/{uid}` document contains `fcmToken`.

The submitted Part 2 configuration contained no OAuth client. Google Sign-In will only work after steps 2â€“6 are completed.

## Testing offline mode

1. Start the app with internet access and allow the catalogue to load.
2. Enable airplane mode or disconnect Wi-Fi and mobile data.
3. Reopen the marketplace. The saved catalogue and offline banner should appear.
4. Create or change an eligible Firestore item while offline.
5. Reconnect. The banner changes to synchronising, current REST data reloads, and Firestore uploads pending writes.

## Testing notifications

1. Sign in and allow Android notification permission.
2. Tap the bell on the home screen to verify the notification channel.
3. Send a chat message to the signed-in account from a second account to test the real-time message notification.
4. In Firebase Console, send a test Cloud Messaging notification to the stored `fcmToken` to verify foreground/background FCM delivery.

## Testing languages

Open Profile > Settings > Language and choose English, Tshivenda or Afrikaans. The app recreates the screen using the selected resources and preserves the choice after restart.

## Build and automated tests

Windows:

```powershell
.\gradlew.bat clean testDebugUnitTest assembleDebug
```

macOS or Linux:

```bash
./gradlew clean testDebugUnitTest assembleDebug
```

The test report is generated in `app/build/reports/tests/testDebugUnitTest/`. The debug APK is generated in `app/build/outputs/apk/debug/`. GitHub Actions runs unit tests, builds the APK and uploads both artifacts on every push or pull request to `main` or `master`.

## Release preparation

Version 2.0 uses `versionCode 2`. Generate a signed Android App Bundle in Android Studio using Build > Generate Signed Bundle or APK > Android App Bundle. Keep the keystore private and never commit it. Store listing text and the final evidence checklist are available in [`docs/play-store-listing.md`](docs/play-store-listing.md). Changes since Part 2 are recorded in [`RELEASE_NOTES.md`](RELEASE_NOTES.md).

## Firebase collections

| Collection | Purpose |
| --- | --- |
| `users` | Profile, email and FCM registration token |
| `listings` | Active, sold and draft student listings |
| `favourites` | Persistent per-user favourites |
| `conversations` | Conversation participants and latest-message summary |
| `messages` | Individual chat messages and notification source |

## Demonstration video

https://youtu.be/UPP6GSY2MHs

## Part 2 video

https://youtu.be/WZzDPLUCt5k

## AI-use disclosure

Our short AI-use disclosure is available in [`docs/AI_USE.md`](docs/AI_USE.md).

