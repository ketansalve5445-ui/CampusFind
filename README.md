# CampusFind Android App (Java)

A beginner-friendly Android Studio project for a college CEP project. It supports reporting lost/found items, browsing/searching listings, and marking items as returned.

## Requirements
- Android Studio (current stable version recommended)
- JDK bundled with Android Studio
- Android SDK Platform 35 (Android Studio can install it when prompted)

## Open and build
1. Extract `CampusFind-Android-Project.zip`.
2. Open Android Studio and choose **Open**; select the `CampusFind` folder (the one containing `settings.gradle`).
3. Allow Gradle sync and install any requested Android SDK components.
4. Run the app on an emulator or USB-connected Android phone.
5. To make an APK, use **Build > Build Bundle(s) / APK(s) > Build APK(s)**. Android Studio will show the generated APK location when the build finishes.

## Features
- Report Lost Item
- Report Found Item
- Search by item name, category, description, or location
- Filter by All / Lost / Found / Open / Returned
- Mark an item as returned
- Local SQLite database; data is stored on that device only

## Important limitations
- This version does not include user accounts, online sync, push notifications, or photo upload.
- Contact information is visible in listings on the device. Use sample/test data during demonstrations and do not publish private student details.
- This is a source project, not a prebuilt APK. Building requires Android Studio and the Android SDK.

## Optional: build the APK automatically on GitHub
This project includes `.github/workflows/build-apk.yml`. If you upload the project to a GitHub repository, the workflow can build a debug APK in GitHub Actions:
1. Create a repository on GitHub and upload the contents of this folder.
2. Open the repository's **Actions** tab and select **Build CampusFind APK**.
3. Click **Run workflow** (or push to the `main`/`master` branch).
4. When the run finishes, open it and download the `CampusFind-debug-apk` artifact. Extract the downloaded artifact ZIP to get `app-debug.apk`.

The APK built this way is a debug APK for testing, not a Play Store release. The workflow has not been run from this environment, so verify the build result in GitHub Actions.
