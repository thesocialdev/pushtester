# Android FCM tester

This is a simple Android application, based on FCM quickstart samples, to debug FCM features during the development of a push notification service.

Features:
- Log FCM token
- Log subscription actions
- Log rae JSON data received from FCM

## Configure google-services.json

In the Android Studio IDE make sure to enable FCM in the application, to do that go to Tools > Firebase > Cloud Messaging > Set Up Firebase Cloud Messaging and follow the steps. The google-services.json file should now by located inside the app folder.

## Download APK

Once this repository is reviewed and considered useful by the PI team, an APK will be available for download to avoid the necessity of building the package
