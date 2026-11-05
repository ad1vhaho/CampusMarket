# CampusMarket Version 2.0 Release Notes

## Added for Part 3

- Google single sign-on integration with Firebase user-profile creation
- Offline REST catalogue caching and visible offline status
- Automatic refresh and Firestore synchronisation after reconnection
- Firebase Cloud Messaging service, notification channel and Android 13+ permission handling
- FCM token storage in the authenticated user's Firestore document
- Real-time notifications for newly received chat messages
- English and Tshivenda language resources with persistent app locale selection
- Five automated tests for offline fallback and language-selection rules
- Google Play Store listing and release-preparation documentation

## Carried forward from Part 2

- Email/password registration, login and password reset
- Editable profiles and secure password changes
- REST API catalogue, search and category filtering
- Active and draft Firestore listings
- Persistent favourites
- Conversations and seller chat
- Dark-mode and notification settings
- Firestore security rules, unit tests and GitHub Actions

## Final verification still required

- Add debug and release SHA fingerprints to Firebase and replace `google-services.json`.
- Test Google Sign-In on a physical device.
- Test FCM foreground and background delivery on a physical device.
- Run the complete Gradle test/build command and save its evidence.
- Generate the signed AAB/APK and capture Play Console preparation evidence.
