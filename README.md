# SafeRide & EduPay (Android)

Private School Tuition Fee Reconciliation, Ethiopian Mobile Money Checkout (`Telebirr` / `Chapa` / `CBE Birr`), and Real-Time School Bus Safety Tracking application built with **Kotlin** and **Jetpack Compose (Material 3)**.

## Features
- **3 Live Role Views:** Switch seamlessly between **School Admin & Finance**, **Bus Attendant / Driver**, and **Parent Portal**.
- **Bilingual (English & Amharic):** Real-time `EN / አማ` toggle across all screens, statuses, notifications, and digital receipts.
- **Biometric & Credential Manager Protection:** **Driver/Attendant** and **Parent** portals are protected by Android `BiometricPrompt` (Fingerprint, Face Unlock, Device Credential) and Jetpack `CredentialManager`.
- **Finance & Analytics:** 16-student ledger (`ETB`), search and multi-filters, monthly collection vs. target chart, bulk SMS/Telegram reminder previews, and manual bank receipt verification.
- **Bus Safety & QR Handoffs:** Simulated live GPS route progression, one-tap boarding/drop-off/absent alerts synced to parents, and temporary authorized pickup QR pass generation & verification.

## Build APK Locally

```bash
gradle :app:testDebugUnitTest --tests com.example.ExampleUnitTest
gradle :app:assembleDebug
```

The compiled debug APK is generated at:
- `app/build/outputs/apk/debug/app-debug.apk`

## Publish to GitHub & Automated APK Releases

This repository includes `.github/workflows/android-apk-release.yml`.

### 1. Push to GitHub
Export/push the project to your GitHub repository (`main` branch). Every push and pull request automatically runs unit tests, builds `saferide-edupay-debug.apk`, and uploads it as a workflow artifact.

### 2. Optional: Enable Signed Release APKs
`app/build.gradle.kts` reads release signing details from environment variables (`keyAlias = "upload"`). To produce signed release APKs in GitHub Actions:

1. Generate an upload keystore (if you do not already have one):
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. Base64-encode the keystore:
   ```bash
   base64 -w 0 my-upload-key.jks
   ```
3. In your GitHub repository go to **Settings → Secrets and variables → Actions** and add:
   - `KEYSTORE_BASE64`: the base64 string of `my-upload-key.jks`
   - `STORE_PASSWORD`: your keystore password
   - `KEY_PASSWORD`: your key password for alias `upload`

### 3. Trigger a GitHub Release
Push a version tag to automatically publish a GitHub Release with the APK(s) attached:
```bash
git tag v1.0.0
git push origin v1.0.0
```
You can also trigger a release manually from the **Actions → Android APK Build & GitHub Release → Run workflow** button.
