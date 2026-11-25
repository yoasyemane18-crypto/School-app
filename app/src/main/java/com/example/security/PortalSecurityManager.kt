package com.example.security

import android.content.Context
import android.content.Intent
import android.content.MutableContextWrapper
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.fragment.app.FragmentActivity
import com.example.model.AppRole
import java.security.MessageDigest
import java.security.SecureRandom

enum class BiometricAvailability {
    AVAILABLE,
    NOT_ENROLLED,
    UNAVAILABLE
}

sealed class SecurityAuthOutcome {
    data class Success(val methodLabel: String) : SecurityAuthOutcome()
    data class Cancelled(val message: String) : SecurityAuthOutcome()
    data class Failure(val message: String, val isNoCredential: Boolean = false) : SecurityAuthOutcome()
}

class PortalSecurityManager(context: Context) {

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("saferide_portal_security_v1", Context.MODE_PRIVATE)

    fun checkBiometricAvailability(): BiometricAvailability {
        val manager = BiometricManager.from(appContext)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
            else -> BiometricAvailability.UNAVAILABLE
        }
    }

    fun hasConfiguredPortalPin(role: AppRole): Boolean {
        val key = pinHashKey(role)
        return !prefs.getString(key, null).isNullOrBlank()
    }

    fun savePortalPin(role: AppRole, rawPin: String): Boolean {
        val trimmed = rawPin.trim()
        if (trimmed.length < 4 || trimmed.length > 8 || !trimmed.all { it.isDigit() }) {
            return false
        }
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = sha256WithSalt(trimmed, salt)
        prefs.edit()
            .putString(pinSaltKey(role), Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(pinHashKey(role), hash)
            .apply()
        return true
    }

    fun verifyPortalPin(role: AppRole, candidatePin: String): Boolean {
        val savedSaltB64 = prefs.getString(pinSaltKey(role), null) ?: return false
        val savedHash = prefs.getString(pinHashKey(role), null) ?: return false
        return try {
            val salt = Base64.decode(savedSaltB64, Base64.NO_WRAP)
            val candidateHash = sha256WithSalt(candidatePin.trim(), salt)
            MessageDigest.isEqual(candidateHash.toByteArray(), savedHash.toByteArray())
        } catch (_: Exception) {
            false
        }
    }

    fun launchBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onResult: (SecurityAuthOutcome) -> Unit
    ) {
        val availability = checkBiometricAvailability()
        if (availability != BiometricAvailability.AVAILABLE) {
            onResult(
                SecurityAuthOutcome.Failure(
                    message = if (availability == BiometricAvailability.NOT_ENROLLED) {
                        "No fingerprint, face unlock, or screen lock is enrolled on this device."
                    } else {
                        "Biometric hardware is unavailable on this device."
                    }
                )
            )
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val label = when (result.authenticationType) {
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> "Biometric Verified"
                        BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> "Device Credential Verified"
                        else -> "Authenticated"
                    }
                    onResult(SecurityAuthOutcome.Success(label))
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_CANCELED
                    ) {
                        onResult(SecurityAuthOutcome.Cancelled(errString.toString()))
                    } else {
                        onResult(SecurityAuthOutcome.Failure(errString.toString()))
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // System keeps prompt open for retries; no final callback yet.
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(false)
            .build()

        prompt.authenticate(promptInfo)
    }

    suspend fun signInWithCredentialManager(
        activity: FragmentActivity,
        role: AppRole
    ): SecurityAuthOutcome {
        val credentialManager = CredentialManager.create(appContext)
        val request = GetCredentialRequest(
            credentialOptions = listOf(GetPasswordOption())
        )
        return try {
            val activityContext = MutableContextWrapper(activity)
            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )
            when (val cred = result.credential) {
                is PublicKeyCredential -> {
                    SecurityAuthOutcome.Success("Passkey / Credential Manager")
                }
                is PasswordCredential -> {
                    if (hasConfiguredPortalPin(role)) {
                        if (verifyPortalPin(role, cred.password)) {
                            SecurityAuthOutcome.Success("Credential Manager (${cred.id})")
                        } else {
                            SecurityAuthOutcome.Failure("Saved credential did not match the ${role.name.lowercase()} portal credential.")
                        }
                    } else {
                        savePortalPin(role, cred.password)
                        SecurityAuthOutcome.Success("Credential Manager (${cred.id})")
                    }
                }
                else -> SecurityAuthOutcome.Success("Credential Manager")
            }
        } catch (e: GetCredentialCancellationException) {
            SecurityAuthOutcome.Cancelled("Credential Manager sign-in cancelled.")
        } catch (e: NoCredentialException) {
            SecurityAuthOutcome.Failure(
                message = "No saved portal credential found in Credential Manager yet.",
                isNoCredential = true
            )
        } catch (e: GetCredentialException) {
            SecurityAuthOutcome.Failure(
                message = e.errorMessage?.toString() ?: "Credential Manager unavailable on this device."
            )
        }
    }

    suspend fun enrollInCredentialManager(
        activity: FragmentActivity,
        role: AppRole,
        accountId: String,
        pinOrPasscode: String
    ): SecurityAuthOutcome {
        if (!savePortalPin(role, pinOrPasscode)) {
            return SecurityAuthOutcome.Failure("Enter a 4 to 8 digit PIN to enroll.")
        }
        val credentialManager = CredentialManager.create(appContext)
        val request = CreatePasswordRequest(
            id = accountId,
            password = pinOrPasscode.trim()
        )
        return try {
            val activityContext = MutableContextWrapper(activity)
            credentialManager.createCredential(
                context = activityContext,
                request = request
            )
            SecurityAuthOutcome.Success("Saved to Credential Manager")
        } catch (e: CreateCredentialCancellationException) {
            SecurityAuthOutcome.Cancelled("Credential save cancelled; local portal PIN is still active.")
        } catch (e: CreateCredentialException) {
            SecurityAuthOutcome.Success("Local Portal PIN active (device Credential Manager provider not configured)")
        }
    }

    fun buildEnrollBiometricSettingsIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
            }
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
    }

    private fun pinSaltKey(role: AppRole) = "portal_pin_salt_${role.name}"
    private fun pinHashKey(role: AppRole) = "portal_pin_hash_${role.name}"

    private fun sha256WithSalt(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashed = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashed, Base64.NO_WRAP)
    }
}
