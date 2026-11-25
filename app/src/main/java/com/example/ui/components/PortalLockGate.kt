package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.security.BiometricAvailability
import com.example.security.PortalSecurityManager
import com.example.security.SecurityAuthOutcome
import com.example.ui.theme.AmberContainerLight
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldContainerLight
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoBright
import com.example.ui.theme.IndigoContainerLight
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.RoseContainerLight
import com.example.ui.theme.RoseError
import kotlinx.coroutines.launch

@Composable
fun PortalLockGate(
    role: AppRole,
    accountLabel: String,
    strings: AppStrings,
    language: AppLanguage,
    securityManager: PortalSecurityManager,
    onUnlocked: (methodLabel: String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val coroutineScope = rememberCoroutineScope()

    var availability by remember { mutableStateOf(securityManager.checkBiometricAvailability()) }
    var hasConfiguredPin by remember(role) { mutableStateOf(securityManager.hasConfiguredPortalPin(role)) }
    var showPinSetup by remember(role) { mutableStateOf(!hasConfiguredPin) }
    var pinInput by remember(role) { mutableStateOf("") }
    var showPinChars by remember { mutableStateOf(false) }
    var isBusy by remember { mutableStateOf(false) }
    var statusMessage by remember(role) { mutableStateOf<String?>(null) }
    var isError by remember(role) { mutableStateOf(false) }

    val title = if (role == AppRole.BUS_ATTENDANT) strings.driverLockTitle else strings.parentLockTitle
    val description = if (role == AppRole.BUS_ATTENDANT) strings.driverLockDesc else strings.parentLockDesc

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .testTag("portal_lock_gate_${role.name}")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Hero Security Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(IndigoDeep, IndigoPrimary)))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Fingerprint,
                                contentDescription = strings.securityGateBadge,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF34D399).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = strings.securityGateBadge,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                                color = Color(0xFFA7F3D0),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Device biometric status pill
                    val (pillBg, pillFg, pillText) = when (availability) {
                        BiometricAvailability.AVAILABLE -> Triple(
                            EmeraldContainerLight,
                            EmeraldSecondary,
                            if (language == AppLanguage.AM) "የስልክ አሻራ / ፊት መክፈቻ ዝግጁ ነው" else "Device Fingerprint / Face Unlock Ready"
                        )
                        BiometricAvailability.NOT_ENROLLED -> Triple(
                            AmberContainerLight,
                            AmberTertiary,
                            if (language == AppLanguage.AM) "በስልኩ ላይ አሻራ ወይም PIN አልተመዘገበም" else "No Fingerprint / Screen Lock Enrolled on Device"
                        )
                        BiometricAvailability.UNAVAILABLE -> Triple(
                            AmberContainerLight,
                            AmberTertiary,
                            if (language == AppLanguage.AM) "ባዮሜትሪክ በዚህ መሣሪያ ላይ የለም — የፖርታል PIN ይጠቀሙ" else "Biometric Hardware Unavailable — Use Credential Manager / PIN"
                        )
                    }

                    Surface(
                        color = pillBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (availability == BiometricAvailability.AVAILABLE) Icons.Filled.CheckCircle else Icons.Filled.Shield,
                                contentDescription = pillText,
                                tint = pillFg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pillText,
                                style = MaterialTheme.typography.labelMedium,
                                color = pillFg
                            )
                        }
                    }

                    // Primary: System BiometricPrompt (Fingerprint / Face Unlock / Device Credential)
                    Button(
                        onClick = {
                            availability = securityManager.checkBiometricAvailability()
                            if (activity == null) {
                                isError = true
                                statusMessage = "Activity context unavailable for BiometricPrompt."
                                return@Button
                            }
                            if (availability != BiometricAvailability.AVAILABLE) {
                                isError = true
                                statusMessage = if (language == AppLanguage.AM) {
                                    "እባክዎ መጀመሪያ የስልክ አሻራ/PIN ይመዝግቡ ወይም ከታች የፖርታል PIN ይፍጠሩ።"
                                } else {
                                    "Device lock is not enrolled yet. Enroll below or set up a Portal Security PIN."
                                }
                                return@Button
                            }
                            isError = false
                            statusMessage = null
                            securityManager.launchBiometricPrompt(
                                activity = activity,
                                title = title,
                                subtitle = accountLabel,
                                onResult = { outcome ->
                                    when (outcome) {
                                        is SecurityAuthOutcome.Success -> onUnlocked(outcome.methodLabel)
                                        is SecurityAuthOutcome.Cancelled -> {
                                            isError = false
                                            statusMessage = outcome.message
                                        }
                                        is SecurityAuthOutcome.Failure -> {
                                            isError = true
                                            statusMessage = outcome.message
                                        }
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("unlock_biometric_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = strings.unlockBiometric,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.unlockBiometric)
                    }

                    // Secondary: Jetpack Credential Manager Sign-In
                    OutlinedButton(
                        onClick = {
                            if (activity == null) return@OutlinedButton
                            isBusy = true
                            isError = false
                            statusMessage = null
                            coroutineScope.launch {
                                val outcome = securityManager.signInWithCredentialManager(activity, role)
                                isBusy = false
                                when (outcome) {
                                    is SecurityAuthOutcome.Success -> onUnlocked(outcome.methodLabel)
                                    is SecurityAuthOutcome.Cancelled -> {
                                        isError = false
                                        statusMessage = outcome.message
                                    }
                                    is SecurityAuthOutcome.Failure -> {
                                        isError = true
                                        statusMessage = outcome.message
                                        if (outcome.isNoCredential) {
                                            showPinSetup = true
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isBusy,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("unlock_credential_manager_button")
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Key, contentDescription = strings.unlockCredentialManager, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.unlockCredentialManager)
                    }

                    if (availability == BiometricAvailability.NOT_ENROLLED) {
                        TextButton(
                            onClick = {
                                try {
                                    context.startActivity(securityManager.buildEnrollBiometricSettingsIntent())
                                } catch (_: Exception) {
                                    isError = true
                                    statusMessage = "Unable to open device security settings on this build."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("open_biometric_settings_button")
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = strings.openSecuritySettings, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.openSecuritySettings)
                        }
                    }

                    if (!statusMessage.isNullOrBlank()) {
                        Surface(
                            color = if (isError) RoseContainerLight else IndigoContainerLight,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = statusMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isError) RoseError else IndigoPrimary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                    // Portal Security PIN + Credential Enrollment Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (hasConfiguredPin) {
                                if (language == AppLanguage.AM) "በፖርታል PIN ይክፈቱ" else "Unlock with Portal PIN"
                            } else {
                                strings.enrollCredentialAction
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (hasConfiguredPin) {
                            TextButton(onClick = { showPinSetup = !showPinSetup }) {
                                Text(
                                    text = if (showPinSetup) {
                                        if (language == AppLanguage.AM) "መክፈቻ" else "Use PIN"
                                    } else {
                                        if (language == AppLanguage.AM) "PIN ቀይር" else "Update Credential"
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it.filter { ch -> ch.isDigit() }.take(8) },
                        label = {
                            Text(
                                if (showPinSetup) {
                                    if (language == AppLanguage.AM) "አዲስ 4–8 አሃዝ PIN ይምረጡ" else "Choose 4–8 digit Portal PIN"
                                } else {
                                    if (language == AppLanguage.AM) "የፖርታል PIN ያስገቡ" else "Enter Portal PIN"
                                }
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (showPinChars) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        trailingIcon = {
                            IconButton(onClick = { showPinChars = !showPinChars }) {
                                Icon(
                                    imageVector = if (showPinChars) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle PIN visibility"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("portal_pin_input")
                    )

                    AnimatedVisibility(visible = showPinSetup) {
                        Button(
                            onClick = {
                                if (activity == null) return@Button
                                isBusy = true
                                isError = false
                                coroutineScope.launch {
                                    val outcome = securityManager.enrollInCredentialManager(
                                        activity = activity,
                                        role = role,
                                        accountId = accountLabel,
                                        pinOrPasscode = pinInput
                                    )
                                    isBusy = false
                                    when (outcome) {
                                        is SecurityAuthOutcome.Success -> {
                                            hasConfiguredPin = true
                                            showPinSetup = false
                                            pinInput = ""
                                            onUnlocked(outcome.methodLabel)
                                        }
                                        is SecurityAuthOutcome.Cancelled -> {
                                            hasConfiguredPin = securityManager.hasConfiguredPortalPin(role)
                                            showPinSetup = false
                                            statusMessage = outcome.message
                                        }
                                        is SecurityAuthOutcome.Failure -> {
                                            isError = true
                                            statusMessage = outcome.message
                                        }
                                    }
                                }
                            },
                            enabled = pinInput.length >= 4 && !isBusy,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoBright),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("enroll_portal_pin_button")
                        ) {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = strings.enrollCredentialAction, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.enrollCredentialAction)
                        }
                    }

                    AnimatedVisibility(visible = !showPinSetup && hasConfiguredPin) {
                        Button(
                            onClick = {
                                if (securityManager.verifyPortalPin(role, pinInput)) {
                                    pinInput = ""
                                    isError = false
                                    onUnlocked("Portal PIN Verified")
                                } else {
                                    isError = true
                                    statusMessage = if (language == AppLanguage.AM) "የተሳሳተ PIN ነው።" else "Incorrect Portal PIN."
                                }
                            },
                            enabled = pinInput.length >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("verify_portal_pin_button")
                        ) {
                            Icon(Icons.Filled.LockOpen, contentDescription = "Unlock with PIN", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (language == AppLanguage.AM) "ክፈት" else "Unlock Portal")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PortalVerifiedBanner(
    authMethodLabel: String,
    strings: AppStrings,
    onLockPortal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = EmeraldContainerLight.copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, EmeraldSecondary.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = strings.portalSecuredBadge,
                    tint = EmeraldSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = strings.portalSecuredBadge,
                        style = MaterialTheme.typography.labelLarge,
                        color = EmeraldSecondary
                    )
                    Text(
                        text = authMethodLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = EmeraldSecondary.copy(alpha = 0.85f)
                    )
                }
            }
            OutlinedButton(
                onClick = onLockPortal,
                border = BorderStroke(1.dp, EmeraldSecondary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .heightIn(min = 40.dp)
                    .testTag("lock_portal_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = strings.lockPortalAction,
                    tint = EmeraldSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = strings.lockPortalAction,
                    style = MaterialTheme.typography.labelMedium,
                    color = EmeraldSecondary
                )
            }
        }
    }
}
