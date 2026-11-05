# CampusMarket Part 3 Final Submission Guide

## Functional evidence

Capture clear phone screenshots or video clips showing registration, email login, Google Sign-In, settings, English, Tshivenda, Afrikaans, REST API products, search/filtering, offline catalogue, reconnection synchronisation, a received notification, listings/drafts, favourites and chat.

## Online service evidence

Show Firebase Authentication users, Firestore collections, an authenticated user document containing an FCM token, published Firestore rules, the REST API response/data, and a successfully received Firebase Cloud Messaging test.

## Testing evidence

Run `gradlew.bat clean testDebugUnitTest assembleDebug` on Windows. Capture `BUILD SUCCESSFUL`, the HTML test summary and the source test folders. Push both feature branches, merge them and capture a green GitHub Actions run.

## Publication evidence

Generate a signed AAB or APK, capture Android Studio's successful generation screen and install the release on a physical phone. Prepare the Play Console app entry, store listing, screenshots, app-content answers and release artifact where institutional access permits.

## Documentation and video

Replace all README placeholders, add the new Part 3 screenshots, insert the unlisted YouTube video link, review the release notes and keep the AI disclosure under 500 words. The video must include voice-over, both members, a physical phone, all required features, Firebase/API/database evidence, automated tests and publication preparation.

## Final repository steps

Confirm that no keystore, password, local properties or private service credentials are committed. Confirm that both members have meaningful commits. Merge into the submission branch, wait for the green workflow, create the `final-PoE` tag and submit the GitHub repository link rather than a ZIP file.

## Lecturer checklist before submission

- Use offline catalogue browsing as Feature 4 and real-time message notifications as Feature 5. Compare both with the three features in your actual Part 2 submission.
- The project uses the third-party HTTPS catalogue at https://api.escuelajs.co/api/v1/products and hosted Firebase services. There is no student-created REST server or localhost setup. Verify both services from the device.
- The five commented cases in Part3AcceptanceTest cover password length, listing price, catalogue search, offline cache and language selection. Each checks both accepted and rejected or fallback inputs.
- Run Android CI on the final commit and capture the successful run link. The Actions tab also supports a manual run.
- Verify the existing repository has at least 10 meaningful commits spread over three days, per the lecturer’s guide. Keep the existing repository history.
- Record the running app with no slides or AI-generated footage. Show a cached catalogue in airplane mode after first loading it online, a received message notification from a second account and the three language choices. Add the video link to the README.
