package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.BoardingStatus
import com.example.model.BusRoute
import com.example.model.PickupPass
import com.example.model.Student
import com.example.model.TripMode
import com.example.ui.components.BoardingStatusBadge
import com.example.ui.components.PortalVerifiedBanner
import com.example.ui.components.QrCodeMatrixCanvas
import com.example.ui.theme.AmberBus
import com.example.ui.theme.AmberContainerLight
import com.example.ui.theme.AmberOnContainerLight
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
fun BusAttendantScreen(
    uiState: SafeRideUiState,
    strings: AppStrings,
    onSelectRoute: (String) -> Unit,
    onToggleTripMode: (String, TripMode) -> Unit,
    onToggleLiveGps: (String) -> Unit,
    onAdvanceStop: (String) -> Unit,
    onMarkBoarding: (String, BoardingStatus) -> Unit,
    onVerifyPickupPass: (String) -> PickupPass?,
    onLockPortal: () -> Unit
) {
    val activeRoute = uiState.selectedRoute
    val routeStudents = remember(uiState.students, activeRoute.id) {
        uiState.students.filter { it.busRouteId == activeRoute.id }
    }
    var showQrVerifyModal by remember { mutableStateOf(false) }

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
                    authMethodLabel = uiState.driverAuthMethod,
                    strings = strings,
                    onLockPortal = onLockPortal
                )
            }
            // Header & Route Selector
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.busHeadline,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.routes.forEach { route ->
                            val selected = route.id == activeRoute.id
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectRoute(route.id) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.DirectionsBus,
                                        contentDescription = route.code,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = {
                                    Text(if (uiState.language == AppLanguage.AM) route.nameAm else route.nameEn)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoContainerLight,
                                    selectedLabelColor = IndigoDeep
                                ),
                                modifier = Modifier
                                    .heightIn(min = 44.dp)
                                    .testTag("route_chip_${route.id}")
                            )
                        }
                    }
                }
            }

            // Trip Mode Toggle (Morning Pickup vs Afternoon Drop-off)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TripModeButton(
                            label = strings.morningPickup,
                            icon = Icons.Filled.WbSunny,
                            selected = activeRoute.tripMode == TripMode.MORNING_PICKUP,
                            onClick = { onToggleTripMode(activeRoute.id, TripMode.MORNING_PICKUP) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trip_mode_morning")
                        )
                        TripModeButton(
                            label = strings.afternoonDropoff,
                            icon = Icons.Filled.NightsStay,
                            selected = activeRoute.tripMode == TripMode.AFTERNOON_DROPOFF,
                            onClick = { onToggleTripMode(activeRoute.id, TripMode.AFTERNOON_DROPOFF) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trip_mode_afternoon")
                        )
                    }
                }
            }

            // Live GPS Route Console Card
            item {
                LiveRouteGpsCard(
                    route = activeRoute,
                    strings = strings,
                    language = uiState.language,
                    onToggleLiveGps = { onToggleLiveGps(activeRoute.id) },
                    onAdvanceStop = { onAdvanceStop(activeRoute.id) }
                )
            }

            // Authorized Pickup QR Verify Trigger Card
            item {
                Card(
                    onClick = { showQrVerifyModal = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberContainerLight),
                    border = BorderStroke(1.dp, AmberBus.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_qr_verify_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AmberTertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = strings.verifyPickupQr,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.verifyPickupQr,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AmberOnContainerLight
                                )
                                Text(
                                    text = if (uiState.language == AppLanguage.AM) {
                                        "${uiState.pickupPasses.count { !it.isVerified }} ንቁ የመውሰጃ ፈቃዶች"
                                    } else {
                                        "${uiState.pickupPasses.count { !it.isVerified }} active passes ready for handoff scan"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmberOnContainerLight.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (uiState.language == AppLanguage.AM) "ቃኝ" else "Verify",
                                style = MaterialTheme.typography.labelLarge,
                                color = AmberTertiary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Boarding Checklist Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.boardingChecklist} (${routeStudents.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val boardedCount = routeStudents.count { it.boardingStatus == BoardingStatus.BOARDED }
                    val droppedCount = routeStudents.count { it.boardingStatus == BoardingStatus.DROPPED_OFF }
                    Text(
                        text = "$boardedCount ${strings.statusBoarded} • $droppedCount ${strings.statusDroppedOff}",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldSecondary
                    )
                }
            }

            items(routeStudents, key = { it.id }) { student ->
                BusStudentChecklistCard(
                    student = student,
                    strings = strings,
                    language = uiState.language,
                    onMarkBoarded = { onMarkBoarding(student.id, BoardingStatus.BOARDED) },
                    onMarkDroppedOff = { onMarkBoarding(student.id, BoardingStatus.DROPPED_OFF) },
                    onMarkAbsent = { onMarkBoarding(student.id, BoardingStatus.ABSENT) }
                )
            }
        }
    }

    if (showQrVerifyModal) {
        QrVerifySimulatorDialog(
            activePasses = uiState.pickupPasses,
            language = uiState.language,
            onDismiss = { showQrVerifyModal = false },
            onVerifyCode = { code -> onVerifyPickupPass(code) }
        )
    }
}

@Composable
private fun TripModeButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) IndigoPrimary else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LiveRouteGpsCard(
    route: BusRoute,
    strings: AppStrings,
    language: AppLanguage,
    onToggleLiveGps: () -> Unit,
    onAdvanceStop: () -> Unit
) {
    val totalStops = route.stops.size.coerceAtLeast(2)
    val overallFraction = ((route.currentStopIndex + route.progressFraction) / (totalStops - 1)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = overallFraction, label = "bus_progress")
    val currentStop = route.stops.getOrNull(route.currentStopIndex)
    val nextStop = route.stops.getOrNull((route.currentStopIndex + 1).coerceAtMost(route.stops.lastIndex))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoDeep),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(colors = listOf(IndigoDeep, IndigoPrimary))
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.busPlate,
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = MonoFontFamily),
                        color = Color(0xFFFDE68A)
                    )
                    Text(
                        text = if (language == AppLanguage.AM) route.nameAm else route.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "${route.driverName} • ${route.attendantName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                }
                Surface(
                    color = if (route.isLiveBroadcasting) EmeraldBright else Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GpsFixed,
                            contentDescription = "GPS Status",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (route.isLiveBroadcasting) strings.liveBroadcastOn else "IDLE",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.AM) "አሁን ያለበት ፌርማታ" else "Current Stop",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (language == AppLanguage.AM) currentStop?.nameAm ?: "" else currentStop?.nameEn ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    if (nextStop != null && nextStop.id != currentStop?.id) {
                        Text(
                            text = "${if (language == AppLanguage.AM) "ቀጣይ፡" else "Next:"} ${if (language == AppLanguage.AM) nextStop.nameAm else nextStop.nameEn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5B4FC)
                        )
                    }
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = strings.etaLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "${route.etaMinutes} ${strings.minsUnit}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AmberBus
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                color = AmberBus,
                trackColor = Color.White.copy(alpha = 0.22f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${route.currentStopIndex + 1} / ${route.stops.size} stops",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                    color = Color.White.copy(alpha = 0.75f)
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggleLiveGps,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (route.isLiveBroadcasting) AmberTertiary else EmeraldSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("toggle_live_gps_button")
                ) {
                    Icon(
                        imageVector = if (route.isLiveBroadcasting) Icons.Filled.PauseCircle else Icons.Filled.PlayArrow,
                        contentDescription = "Toggle GPS Broadcast",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (route.isLiveBroadcasting) strings.stopRouteGps else strings.startRouteGps,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onAdvanceStop,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("advance_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = strings.advanceStop,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.advanceStop)
                }
            }
        }
    }
}

@Composable
private fun BusStudentChecklistCard(
    student: Student,
    strings: AppStrings,
    language: AppLanguage,
    onMarkBoarded: () -> Unit,
    onMarkDroppedOff: () -> Unit,
    onMarkAbsent: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bus_student_card_${student.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.AM) student.amharicName else student.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${student.grade} • ${student.pickupStopName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${student.lastBoardingTimestamp} • ${student.lastBoardingNote}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary
                    )
                }
                BoardingStatusBadge(status = student.boardingStatus, strings = strings)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkBoarded,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (student.boardingStatus == BoardingStatus.BOARDED) EmeraldSecondary else EmeraldContainerLight,
                        contentColor = if (student.boardingStatus == BoardingStatus.BOARDED) Color.White else EmeraldSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("mark_boarded_${student.id}")
                ) {
                    Icon(Icons.Filled.Login, contentDescription = strings.markBoarded, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.markBoarded, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Button(
                    onClick = onMarkDroppedOff,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (student.boardingStatus == BoardingStatus.DROPPED_OFF) IndigoPrimary else IndigoContainerLight,
                        contentColor = if (student.boardingStatus == BoardingStatus.DROPPED_OFF) Color.White else IndigoPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("mark_dropped_${student.id}")
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = strings.markDroppedOff, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.markDroppedOff, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Button(
                    onClick = onMarkAbsent,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (student.boardingStatus == BoardingStatus.ABSENT) RoseError else RoseContainerLight,
                        contentColor = if (student.boardingStatus == BoardingStatus.ABSENT) Color.White else RoseError
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .heightIn(min = 48.dp)
                        .testTag("mark_absent_${student.id}")
                ) {
                    Icon(Icons.Filled.EventBusy, contentDescription = strings.markAbsent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.markAbsent, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun QrVerifySimulatorDialog(
    activePasses: List<PickupPass>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onVerifyCode: (String) -> PickupPass?
) {
    var codeInput by remember { mutableStateOf(activePasses.firstOrNull { !it.isVerified }?.code ?: "SR-4821") }
    var verificationResult by remember { mutableStateOf<PickupPass?>(null) }
    var notFoundError by remember { mutableStateOf(false) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = "QR Verify",
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.AM) "የተወካይ QR ኮድ ማረጋገጫ" else "Authorized Pickup QR Verifier",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it
                        notFoundError = false
                    },
                    label = { Text(if (language == AppLanguage.AM) "የመውሰጃ ኮድ (ለምሳሌ SR-4821)" else "Enter or Scan Pass Code (e.g. SR-4821)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qr_code_verify_input")
                )

                // Quick tap simulated passes
                if (activePasses.isNotEmpty()) {
                    Text(
                        text = if (language == AppLanguage.AM) "ንቁ የወላጅ ፈቃዶች (ለመቃኘት ይንኩ)፦" else "Active Parent Passes (tap to simulate scan):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    activePasses.take(4).forEach { pass ->
                        Surface(
                            onClick = {
                                codeInput = pass.code
                                notFoundError = false
                            },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${pass.code} • ${pass.studentName}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = MonoFontFamily)
                                    )
                                    Text(
                                        text = "${pass.delegateName} (${pass.relationship})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (pass.isVerified) {
                                    Surface(color = EmeraldContainerLight, shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = "VERIFIED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    QrCodeMatrixCanvas(payload = pass.code, sizeDp = 42.dp)
                                }
                            }
                        }
                    }
                }

                if (notFoundError) {
                    Text(
                        text = if (language == AppLanguage.AM) "የተሳሳተ የመውሰጃ ኮድ ነው።" else "No matching active pickup pass found for this code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoseError
                    )
                }

                if (verificationResult != null) {
                    val res = verificationResult!!
                    Surface(
                        color = EmeraldContainerLight,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Verified", tint = EmeraldSecondary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.AM) "ርክክብ ተረጋግጧል!" else "Handoff Verified & Logged!",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = EmeraldSecondary
                                )
                                Text(
                                    text = "${res.studentName} → ${res.delegateName} (${res.code})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

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
                        Text(if (language == AppLanguage.AM) "ዝጋ" else "Close")
                    }
                    Button(
                        onClick = {
                            val res = onVerifyCode(codeInput)
                            if (res != null) {
                                verificationResult = res
                                notFoundError = false
                            } else {
                                notFoundError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("confirm_qr_handoff_button")
                    ) {
                        Text(if (language == AppLanguage.AM) "ርክክብ አረጋግጥ" else "Confirm Handoff")
                    }
                }
            }
        }
    }
}
