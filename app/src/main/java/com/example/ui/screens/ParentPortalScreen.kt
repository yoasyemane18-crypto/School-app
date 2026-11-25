package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.BoardingStatus
import com.example.model.BusRoute
import com.example.model.PaymentMethod
import com.example.model.PaymentReceipt
import com.example.model.PickupPass
import com.example.model.SafetyNotification
import com.example.model.Student
import com.example.model.TripMode
import com.example.ui.components.BoardingStatusBadge
import com.example.ui.components.PortalVerifiedBanner
import com.example.ui.components.QrCodeMatrixCanvas
import com.example.ui.components.formatEtb
import com.example.ui.theme.AmberBus
import com.example.ui.theme.AmberContainerLight
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainerLight
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoBright
import com.example.ui.theme.IndigoContainerLight
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.RoseContainerLight
import com.example.ui.theme.RoseError
import com.example.viewmodel.SafeRideUiState

@Composable
fun ParentPortalScreen(
    uiState: SafeRideUiState,
    strings: AppStrings,
    onSelectParent: (String) -> Unit,
    onPayMobileMoney: (parentId: String, method: PaymentMethod, phone: String) -> Unit,
    onOpenReceipt: (PaymentReceipt) -> Unit,
    onGeneratePickupPass: (studentId: String, delegateName: String, relation: String, phone: String) -> Unit,
    onShowMessage: (String) -> Unit,
    onLockPortal: () -> Unit
) {
    val context = LocalContext.current
    val parent = uiState.selectedParent
    val children = remember(uiState.students, parent.id) {
        uiState.students.filter { it.parentId == parent.id }
    }
    val parentBusRoute = remember(uiState.routes, children, parent) {
        val routeId = children.firstOrNull()?.busRouteId ?: parent.primaryRouteId
        uiState.routes.firstOrNull { it.id == routeId } ?: uiState.routes.first()
    }
    val parentNotifications = remember(uiState.notifications, parent.id) {
        uiState.notifications.filter { it.parentId == parent.id }
    }
    val parentReceipts = remember(uiState.receipts, children) {
        val childIds = children.map { it.id }.toSet()
        uiState.receipts.filter { it.studentId in childIds }
    }
    val parentPasses = remember(uiState.pickupPasses, parent.id) {
        uiState.pickupPasses.filter { it.parentId == parent.id }
    }

    val totalTuition = children.sumOf { it.tuitionFeeEtb }
    val totalBus = children.sumOf { it.busFeeEtb }
    val totalPaid = children.sumOf { it.totalPaidEtb }
    val totalBalance = children.sumOf { it.balanceDueEtb }

    var showCheckoutModal by remember { mutableStateOf(false) }
    var showPickupPassModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PortalVerifiedBanner(
                    authMethodLabel = uiState.parentAuthMethod,
                    strings = strings,
                    onLockPortal = onLockPortal
                )
            }
            // Parent Profile Switcher
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.parentHeadline,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = strings.switchFamily,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.parentProfiles.forEach { profile ->
                            val familyStudents = uiState.students.filter { it.parentId == profile.id }
                            val firstNames = familyStudents.joinToString(" & ") {
                                (if (uiState.language == AppLanguage.AM) it.amharicName else it.fullName).substringBefore(" ")
                            }
                            FilterChip(
                                selected = profile.id == parent.id,
                                onClick = { onSelectParent(profile.id) },
                                label = {
                                    Text(
                                        text = if (uiState.language == AppLanguage.AM) "የ$firstNames ወላጅ" else "Parent of $firstNames"
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoContainerLight,
                                    selectedLabelColor = IndigoDeep
                                ),
                                modifier = Modifier
                                    .heightIn(min = 44.dp)
                                    .testTag("parent_profile_${profile.id}")
                            )
                        }
                    }
                }
            }

            // Children Live Status Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    children.forEach { child ->
                        ChildLiveStatusCard(
                            child = child,
                            route = parentBusRoute,
                            strings = strings,
                            language = uiState.language
                        )
                    }
                }
            }

            // Live Bus Tracker & Stop Timeline
            item {
                ParentBusTrackerCard(
                    route = parentBusRoute,
                    strings = strings,
                    language = uiState.language,
                    onCallAttendant = {
                        try {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${parentBusRoute.attendantPhone}"))
                            context.startActivity(dialIntent)
                        } catch (_: Exception) {
                            onShowMessage("Calling ${parentBusRoute.attendantName} (${parentBusRoute.attendantPhone})")
                        }
                    }
                )
            }

            // Live Safety Feed (Synced directly from Bus App)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = strings.safetyFeed,
                                tint = EmeraldSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.safetyFeed,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (parentNotifications.isEmpty()) {
                            Text(
                                text = if (uiState.language == AppLanguage.AM) "እስካሁን አዲስ የጉዞ መልዕክት የለም።" else "No boarding updates yet for today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            parentNotifications.take(5).forEach { notif ->
                                SafetyNotificationRow(notif = notif, language = uiState.language)
                            }
                        }
                    }
                }
            }

            // Fee Invoice & One-Click Mobile Money Checkout
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.feeInvoiceWallet,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Surface(
                                color = if (totalBalance == 0) EmeraldContainerLight else AmberContainerLight,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (totalBalance == 0) strings.statusPaid else strings.statusPartial,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (totalBalance == 0) EmeraldSecondary else AmberTertiary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        InvoiceLine(
                            label = if (uiState.language == AppLanguage.AM) "የትምህርት ክፍያ (${children.size} ልጆች)" else "Tuition Fee (${children.size} children)",
                            amount = formatEtb(totalTuition, uiState.language)
                        )
                        InvoiceLine(
                            label = if (uiState.language == AppLanguage.AM) "የአውቶቡስ ትራንስፖርት (${parentBusRoute.code})" else "Bus Transport (${parentBusRoute.code})",
                            amount = formatEtb(totalBus, uiState.language)
                        )
                        InvoiceLine(
                            label = strings.totalPaid,
                            amount = "- ${formatEtb(totalPaid, uiState.language)}",
                            valueColor = EmeraldSecondary
                        )
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.balanceDue,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = formatEtb(totalBalance, uiState.language),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (totalBalance > 0) RoseError else EmeraldSecondary
                            )
                        }

                        if (totalBalance > 0) {
                            Button(
                                onClick = { showCheckoutModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .testTag("parent_pay_now_button")
                            ) {
                                Icon(Icons.Filled.Payment, contentDescription = strings.payNowMobileMoney)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.payNowMobileMoney)
                            }
                        } else {
                            Surface(
                                color = EmeraldContainerLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = strings.paidInFull, tint = EmeraldSecondary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.paidInFull,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = EmeraldSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Receipt Wallet
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ReceiptLong, contentDescription = strings.receiptWallet, tint = IndigoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.receiptWallet, style = MaterialTheme.typography.titleMedium)
                        }
                        if (parentReceipts.isEmpty()) {
                            Text(
                                text = if (uiState.language == AppLanguage.AM) "እስካሁን ደረሰኝ የለም።" else "No receipts issued for this family yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            parentReceipts.forEach { rc ->
                                Surface(
                                    onClick = { onOpenReceipt(rc) },
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("wallet_receipt_${rc.receiptNumber}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${rc.receiptNumber} • ${rc.studentName}",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                            Text(
                                                text = "${rc.method.displayName} • ${rc.timestamp}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = formatEtb(rc.amountPaidEtb, uiState.language),
                                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = MonoFontFamily),
                                            color = EmeraldSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Temporary Authorized Pickup Pass Generator
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Badge, contentDescription = strings.pickupPassTitle, tint = AmberTertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.pickupPassTitle, style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        parentPasses.forEach { pass ->
                            Surface(
                                color = if (pass.isVerified) EmeraldContainerLight.copy(alpha = 0.55f) else IndigoContainerLight.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pass.code,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = MonoFontFamily,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = IndigoDeep
                                        )
                                        Text(
                                            text = "${pass.studentName} → ${pass.delegateName}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = "${pass.relationship} • ${pass.delegatePhone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = if (pass.isVerified) "Verified Handoff at ${pass.verifiedAt}" else pass.validDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (pass.isVerified) EmeraldSecondary else AmberTertiary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    QrCodeMatrixCanvas(payload = "${pass.code}|${pass.studentId}", sizeDp = 74.dp)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showPickupPassModal = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("open_generate_pickup_pass_button")
                        ) {
                            Icon(Icons.Filled.QrCode2, contentDescription = strings.generatePickupPass)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.generatePickupPass)
                        }
                    }
                }
            }
        }
    }

    if (showCheckoutModal) {
        MobileMoneyCheckoutDialog(
            amountEtb = totalBalance,
            defaultPhone = parent.phone,
            language = uiState.language,
            onDismiss = { showCheckoutModal = false },
            onConfirmPayment = { method, phone ->
                onPayMobileMoney(parent.id, method, phone)
                showCheckoutModal = false
            }
        )
    }

    if (showPickupPassModal && children.isNotEmpty()) {
        GeneratePickupPassDialog(
            children = children,
            strings = strings,
            language = uiState.language,
            onDismiss = { showPickupPassModal = false },
            onGenerate = { studentId, name, relation, phone ->
                onGeneratePickupPass(studentId, name, relation, phone)
                showPickupPassModal = false
            }
        )
    }
}

@Composable
private fun ChildLiveStatusCard(
    child: Student,
    route: BusRoute,
    strings: AppStrings,
    language: AppLanguage
) {
    val statusLine = when (child.boardingStatus) {
        BoardingStatus.BOARDED -> if (language == AppLanguage.AM) {
            "በ${route.code} አውቶቡስ ላይ • ወደ ት/ቤት በመጓዝ ላይ (${child.lastBoardingTimestamp})"
        } else {
            "On ${route.code} Bus - En Route • ${child.lastBoardingTimestamp}"
        }
        BoardingStatus.DROPPED_OFF -> if (language == AppLanguage.AM) {
            "በሰላም ደርሷል • ${child.lastBoardingTimestamp}"
        } else {
            "${child.lastBoardingNote} at ${child.lastBoardingTimestamp}"
        }
        BoardingStatus.ABSENT -> if (language == AppLanguage.AM) {
            "ዛሬ ከአውቶቡስ ቀርቷል ተብሎ ተመዝግቧል"
        } else {
            "Marked Absent Today (${child.lastBoardingTimestamp})"
        }
        BoardingStatus.WAITING -> if (language == AppLanguage.AM) {
            "በ${child.pickupStopName} ፌርማታ በመጠበቅ ላይ"
        } else {
            "Waiting at ${child.pickupStopName}"
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(IndigoContainerLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = "Child safety", tint = IndigoPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${if (language == AppLanguage.AM) child.amharicName else child.fullName} • ${child.grade}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = statusLine,
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            BoardingStatusBadge(status = child.boardingStatus, strings = strings)
        }
    }
}

@Composable
private fun ParentBusTrackerCard(
    route: BusRoute,
    strings: AppStrings,
    language: AppLanguage,
    onCallAttendant: () -> Unit
) {
    val totalStops = route.stops.size.coerceAtLeast(2)
    val overallFraction = ((route.currentStopIndex + route.progressFraction) / (totalStops - 1)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = overallFraction, label = "parent_bus_prog")

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DirectionsBus, contentDescription = strings.liveBusTracker, tint = IndigoPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.liveBusTracker, style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "${if (language == AppLanguage.AM) route.nameAm else route.nameEn} • ${if (route.tripMode == TripMode.MORNING_PICKUP) strings.morningPickup else strings.afternoonDropoff}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = if (route.isLiveBroadcasting) EmeraldContainerLight else AmberContainerLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${route.etaMinutes} ${strings.minsUnit}",
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = MonoFontFamily),
                        color = if (route.isLiveBroadcasting) EmeraldSecondary else AmberTertiary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                color = EmeraldBright,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            // Stop Timeline
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                route.stops.forEachIndexed { idx, stop ->
                    val passed = idx < route.currentStopIndex
                    val current = idx == route.currentStopIndex
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(if (current) 14.dp else 10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            current -> AmberBus
                                            passed -> EmeraldSecondary
                                            else -> MaterialTheme.colorScheme.outline
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.AM) stop.nameAm else stop.nameEn,
                                style = if (current) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall,
                                color = if (current) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (route.tripMode == TripMode.MORNING_PICKUP) stop.scheduledTimeMorning else stop.scheduledTimeAfternoon,
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.attendantName,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "${route.attendantPhone} • ${route.driverName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = onCallAttendant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("call_attendant_button")
                ) {
                    Icon(Icons.Filled.Call, contentDescription = strings.callAttendant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.callAttendant)
                }
            }
        }
    }
}

@Composable
private fun SafetyNotificationRow(
    notif: SafetyNotification,
    language: AppLanguage
) {
    val dotColor = when (notif.type) {
        BoardingStatus.BOARDED -> AmberBus
        BoardingStatus.DROPPED_OFF -> EmeraldSecondary
        BoardingStatus.ABSENT -> RoseError
        BoardingStatus.WAITING -> IndigoBright
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == AppLanguage.AM) notif.titleAm else notif.titleEn,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = notif.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (language == AppLanguage.AM) notif.detailAm else notif.detailEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InvoiceLine(
    label: String,
    amount: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}

@Composable
private fun MobileMoneyCheckoutDialog(
    amountEtb: Int,
    defaultPhone: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmPayment: (method: PaymentMethod, phone: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.TELEBIRR) }
    var phoneInput by remember { mutableStateOf(defaultPhone) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(IndigoDeep, IndigoPrimary)))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.AM) "የሞባይል ገንዘብ ክፍያ" else "Ethiopian Mobile Money Checkout",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFDE68A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatEtb(amountEtb, language),
                            style = MaterialTheme.typography.displaySmall.copy(fontFamily = MonoFontFamily),
                            color = Color.White
                        )
                    }
                }

                Text(
                    text = if (language == AppLanguage.AM) "የክፍያ አገልግሎት ይምረጡ" else "Choose Payment Gateway",
                    style = MaterialTheme.typography.titleSmall
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(PaymentMethod.TELEBIRR, PaymentMethod.CHAPA, PaymentMethod.CBE_BIRR).forEach { method ->
                        val selected = selectedMethod == method
                        Surface(
                            onClick = { selectedMethod = method },
                            color = if (selected) EmeraldContainerLight else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) EmeraldSecondary else MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("gateway_${method.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.PhoneAndroid,
                                        contentDescription = method.displayName,
                                        tint = if (selected) EmeraldSecondary else IndigoPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (language == AppLanguage.AM) method.amharicName else method.displayName,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                if (selected) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = EmeraldSecondary)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text(if (language == AppLanguage.AM) "የስልክ / የአካውንት ቁጥር" else "Wallet Phone / Account") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Text(if (language == AppLanguage.AM) "ሰርዝ" else "Cancel")
                    }
                    Button(
                        onClick = { onConfirmPayment(selectedMethod, phoneInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("confirm_mobile_money_payment")
                    ) {
                        Text(if (language == AppLanguage.AM) "አሁን ክፈል" else "Confirm & Pay")
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneratePickupPassDialog(
    children: List<Student>,
    strings: AppStrings,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onGenerate: (studentId: String, name: String, relation: String, phone: String) -> Unit
) {
    var selectedChildId by remember { mutableStateOf(children.first().id) }
    var delegateName by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var delegatePhone by remember { mutableStateOf("+251 9") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.generatePickupPass,
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    children.forEach { child ->
                        FilterChip(
                            selected = selectedChildId == child.id,
                            onClick = { selectedChildId = child.id },
                            label = { Text(if (language == AppLanguage.AM) child.amharicName else child.fullName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = delegateName,
                    onValueChange = { delegateName = it },
                    label = { Text(strings.delegateNameLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pickup_delegate_name_input")
                )

                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text(strings.relationLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pickup_delegate_relation_input")
                )

                OutlinedTextField(
                    value = delegatePhone,
                    onValueChange = { delegatePhone = it },
                    label = { Text(strings.delegatePhoneLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Text(if (language == AppLanguage.AM) "ሰርዝ" else "Cancel")
                    }
                    Button(
                        onClick = {
                            if (delegateName.isNotBlank()) {
                                onGenerate(selectedChildId, delegateName, relation, delegatePhone)
                            }
                        },
                        enabled = delegateName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("confirm_generate_pickup_pass")
                    ) {
                        Text(if (language == AppLanguage.AM) "ፈቃድ ፍጠር" else "Issue Pass")
                    }
                }
            }
        }
    }
}
