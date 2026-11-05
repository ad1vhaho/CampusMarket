# Part 3 Work Split for Two Members

## Member 1 Adivhaho Mphaphuli

- Configure and demonstrate Google Sign-In in Firebase.
- Own the offline implementation in `offline`, `ProductRepository`, `HomeViewModel` and the listing write queue.
- Run the offline tests and capture online, offline and reconnection evidence.
- Review Firestore rules and Firebase data after synchronisation.
- Commit on `feature/google-offline` through Adivhaho's GitHub account.

Suggested commits:

1. `Add Google SSO user profile integration`
2. `Cache REST catalogue for offline access`
3. `Queue Firestore listings for reconnection sync`
4. `Add offline mode automated tests`

## Member 2 Dineo Makgatho

- Own `notifications`, FCM configuration and notification permission testing.
- Own `values-ve`, locale configuration and the language selector.
- Test notification delivery in foreground/background and from a second chat account.
- Capture English, Tshivenda, FCM token and received-notification evidence.
- Commit on `feature/notifications-language` through Dineo's GitHub account.

Suggested commits:

1. `Add Firebase Cloud Messaging integration`
2. `Notify users about new chat messages`
3. `Add English and Tshivenda language resources`
4. `Add supported language automated tests`

## Shared work

- Compare the final app against the original Part 1 user-defined features.
- Test all Part 2 functions on a physical Android phone.
- Complete the Play Store listing, signed bundle and release evidence.
- Review release notes, README and AI-use disclosure.
- Record the final demonstration video, with each member explaining their own work.
- Merge approved branches into `master`, run GitHub Actions and tag the final commit `final-PoE`.
