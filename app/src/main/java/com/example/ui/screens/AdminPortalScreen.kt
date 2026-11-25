package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.MonthlyCollectionStat
import com.example.model.PaymentMethod
import com.example.model.PaymentReceipt
import com.example.model.PaymentStatus
import com.example.model.Student
import com.example.ui.components.PaymentStatusBadge
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
fun AdminPortalScreen(
    uiState: SafeRideUiState,
    strings: AppStrings,
    onSearchChange: (String) -> Unit,
    onStatusFilter: (PaymentStatus?) -> Unit,
    onGradeFilter: (String?) -> Unit,
    onRouteFilter: (String?) -> Unit,
    onRecordManualReceipt: (studentId: String, amountEtb: Int, method: PaymentMethod, ref: String) -> Unit,
    onDispatchBulkReminders: (Int) -> Unit,
    onOpenReceipt: (PaymentReceipt) -> Unit
) {
    var showBulkReminderModal by remember { mutableStateOf(false) }
    var recordPaymentTargetStudent by remember { mutableStateOf<Student?>(null) }
    var showGenericRecordModal by remember { mutableStateOf(false) }

    val overdueOrPartial = remember(uiState.students) {
        uiState.students.filter { it.balanceDueEtb > 0 }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.height(124.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_school_bus_banner),
                            contentDescription = "Addis Ababa School Bus Campus Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            IndigoDeep.copy(alpha = 0.90f),
                                            IndigoPrimary.copy(alpha = 0.65f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = strings.adminHeadline,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (uiState.language == AppLanguage.AM) {
                                        "ቀጥታ የቴሌብር፣ ሲቢኢ ብር እና የአውቶቡስ ክትትል ዳሽቦርድ"
                                    } else {
                                        "Live Telebirr, CBE Birr & Fleet Reconciliation Console"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.88f)
                                )
                            }
                        }
                    }
                }
            }

            // 2x2 KPI Summary Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiCard(
                            title = strings.totalStudents,
                            value = "${uiState.totalEnrolled}",
                            subtitle = if (uiState.language == AppLanguage.AM) "KG-1 እስከ 8ኛ ክፍል" else "KG-1 to Grade 8",
                            icon = Icons.Filled.Group,
                            accentColor = IndigoPrimary,
                            containerBg = IndigoContainerLight,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = strings.feesCollected,
                            value = formatEtb(uiState.totalCollectedEtb, uiState.language),
                            subtitle = "${uiState.receipts.size} ${if (uiState.language == AppLanguage.AM) "ደረሰኞች" else "receipts"}",
                            icon = Icons.Filled.AccountBalanceWallet,
                            accentColor = EmeraldSecondary,
                            containerBg = EmeraldContainerLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiCard(
                            title = strings.pendingBalances,
                            value = formatEtb(uiState.totalPendingEtb, uiState.language),
                            subtitle = "${overdueOrPartial.size} ${if (uiState.language == AppLanguage.AM) "ያልተጠናቀቁ" else "accounts due"}",
                            icon = Icons.Filled.WarningAmber,
                            accentColor = AmberTertiary,
                            containerBg = AmberContainerLight,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = strings.activeBuses,
                            value = "${uiState.activeBusCount} / ${uiState.routes.size}",
                            subtitle = if (uiState.language == AppLanguage.AM) "ቀጥታ GPS በስራ ላይ" else "Live GPS active",
                            icon = Icons.Filled.DirectionsBus,
                            accentColor = IndigoBright,
                            containerBg = IndigoContainerLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Monthly Collection Analytics Chart
            item {
                CollectionAnalyticsCard(
                    stats = uiState.monthlyStats,
                    language = uiState.language,
                    strings = strings
                )
            }

            // Admin Bulk Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showBulkReminderModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberTertiary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("bulk_reminder_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Campaign,
                            contentDescription = strings.sendBulkReminder,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.sendBulkReminder,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = { showGenericRecordModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("record_bank_receipt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payments,
                            contentDescription = strings.recordBankReceipt,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.recordBankReceipt,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Search + Multi-Filter Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${strings.studentLedger} (${uiState.filteredStudents.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text(strings.searchPlaceholder, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search Ledger")
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_search_input")
                    )

                    // Status Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.statusFilter == null,
                            onClick = { onStatusFilter(null) },
                            label = { Text(strings.filterAll) },
                            modifier = Modifier.testTag("filter_status_all")
                        )
                        FilterChip(
                            selected = uiState.statusFilter == PaymentStatus.PAID,
                            onClick = { onStatusFilter(PaymentStatus.PAID) },
                            label = { Text(strings.statusPaid) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldContainerLight),
                            modifier = Modifier.testTag("filter_status_paid")
                        )
                        FilterChip(
                            selected = uiState.statusFilter == PaymentStatus.PARTIAL,
                            onClick = { onStatusFilter(PaymentStatus.PARTIAL) },
                            label = { Text(strings.statusPartial) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AmberContainerLight),
                            modifier = Modifier.testTag("filter_status_partial")
                        )
                        FilterChip(
                            selected = uiState.statusFilter == PaymentStatus.OVERDUE,
                            onClick = { onStatusFilter(PaymentStatus.OVERDUE) },
                            label = { Text(strings.statusOverdue) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoseContainerLight),
                            modifier = Modifier.testTag("filter_status_overdue")
                        )
                    }

                    // Grade & Bus Route Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            null to (if (uiState.language == AppLanguage.AM) "ሁሉም ክፍሎች" else "All Grades"),
                            "KG" to "KG-1..KG-2",
                            "G1-4" to "Grade 1–4",
                            "G5-8" to "Grade 5–8"
                        ).forEach { (key, label) ->
                            FilterChip(
                                selected = uiState.gradeFilter == key,
                                onClick = { onGradeFilter(key) },
                                label = { Text(label) }
                            )
                        }
                        listOf(
                            null to (if (uiState.language == AppLanguage.AM) "ሁሉም መስመሮች" else "All Routes"),
                            "ROUTE-A" to "Route A",
                            "ROUTE-B" to "Route B",
                            "ROUTE-C" to "Route C"
                        ).forEach { (key, label) ->
                            FilterChip(
                                selected = uiState.routeFilter == key,
                                onClick = { onRouteFilter(key) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            if (uiState.filteredStudents.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "No results",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.language == AppLanguage.AM) "ምንም ተማሪ አልተገኘም" else "No matching student records",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            } else {
                items(uiState.filteredStudents, key = { it.id }) { student ->
                    val latestReceipt = uiState.receipts.firstOrNull { it.studentId == student.id }
                    StudentLedgerCard(
                        student = student,
                        strings = strings,
                        language = uiState.language,
                        latestReceipt = latestReceipt,
                        onRecordPayment = { recordPaymentTargetStudent = student },
                        onViewReceipt = { receipt -> onOpenReceipt(receipt) }
                    )
                }
            }
        }
    }

    if (showBulkReminderModal) {
        BulkReminderDialog(
            pendingStudents = overdueOrPartial,
            language = uiState.language,
            onDismiss = { showBulkReminderModal = false },
            onSendAll = {
                onDispatchBulkReminders(overdueOrPartial.size)
                showBulkReminderModal = false
            }
        )
    }

    val modalStudent = recordPaymentTargetStudent ?: if (showGenericRecordModal) {
        overdueOrPartial.firstOrNull() ?: uiState.students.firstOrNull()
    } else null

    if (modalStudent != null) {
        RecordBankReceiptDialog(
            initialStudent = modalStudent,
            allStudents = uiState.students,
            language = uiState.language,
            onDismiss = {
                recordPaymentTargetStudent = null
                showGenericRecordModal = false
            },
            onConfirm = { studentId, amount, method, ref ->
                onRecordManualReceipt(studentId, amount, method, ref)
                recordPaymentTargetStudent = null
                showGenericRecordModal = false
            }
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    containerBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(containerBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CollectionAnalyticsCard(
    stats: List<MonthlyCollectionStat>,
    language: AppLanguage,
    strings: AppStrings
) {
    var selectedIdx by remember { mutableIntStateOf(stats.lastIndex.coerceAtLeast(0)) }
    val selectedStat = stats.getOrNull(selectedIdx)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = strings.monthlyAnalytics,
                        tint = EmeraldSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.monthlyAnalytics,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (selectedStat != null) {
                    val pct = ((selectedStat.collectedEtb.toFloat() / selectedStat.targetEtb.coerceAtLeast(1)) * 100).toInt()
                    Surface(
                        color = if (pct >= 90) EmeraldContainerLight else AmberContainerLight,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "$pct%",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = MonoFontFamily),
                            color = if (pct >= 90) EmeraldSecondary else AmberTertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (selectedStat != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (language == AppLanguage.AM) selectedStat.monthAm else selectedStat.monthEn}: ${formatEtb(selectedStat.collectedEtb, language)} / ${formatEtb(selectedStat.targetEtb, language)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = MonoFontFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val maxVal = remember(stats) {
                (stats.maxOfOrNull { maxOf(it.collectedEtb, it.targetEtb) } ?: 300000).toFloat()
            }

            // Chart Canvas
            val barColor = IndigoBright
            val barHighlight = EmeraldBright
            val targetLineColor = AmberBus
            val gridColor = MaterialTheme.colorScheme.outline

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                val chartHeight = size.height - 12f
                val slotWidth = size.width / stats.size.coerceAtLeast(1)
                val barWidth = slotWidth * 0.44f

                // Horizontal grid lines
                listOf(0.25f, 0.5f, 0.75f, 1.0f).forEach { frac ->
                    val y = chartHeight * (1f - frac)
                    drawLine(
                        color = gridColor.copy(alpha = 0.55f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.5f
                    )
                }

                // Target line path
                val targetPath = Path()
                stats.forEachIndexed { idx, stat ->
                    val centerX = slotWidth * idx + slotWidth / 2f
                    val y = chartHeight * (1f - (stat.targetEtb / maxVal).coerceIn(0f, 1f))
                    if (idx == 0) targetPath.moveTo(centerX, y) else targetPath.lineTo(centerX, y)

                    // Collected bar
                    val barHeight = chartHeight * (stat.collectedEtb / maxVal).coerceIn(0f, 1f)
                    val topY = chartHeight - barHeight
                    drawRoundRect(
                        color = if (idx == selectedIdx) barHighlight else barColor.copy(alpha = 0.85f),
                        topLeft = Offset(centerX - barWidth / 2f, topY),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }

                drawPath(
                    path = targetPath,
                    color = targetLineColor,
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )

                stats.forEachIndexed { idx, stat ->
                    val centerX = slotWidth * idx + slotWidth / 2f
                    val y = chartHeight * (1f - (stat.targetEtb / maxVal).coerceIn(0f, 1f))
                    drawCircle(
                        color = Color.White,
                        radius = 7f,
                        center = Offset(centerX, y)
                    )
                    drawCircle(
                        color = targetLineColor,
                        radius = 4.5f,
                        center = Offset(centerX, y)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.forEachIndexed { idx, stat ->
                    Surface(
                        onClick = { selectedIdx = idx },
                        color = if (idx == selectedIdx) IndigoContainerLight else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AM) stat.monthAm else stat.monthEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (idx == selectedIdx) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendDot(color = IndigoBright, label = if (language == AppLanguage.AM) "የተሰበሰበ" else "Collected")
                LegendDot(color = AmberBus, label = if (language == AppLanguage.AM) "ወርሃዊ ዒላማ" else "Target")
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StudentLedgerCard(
    student: Student,
    strings: AppStrings,
    language: AppLanguage,
    latestReceipt: PaymentReceipt?,
    onRecordPayment: () -> Unit,
    onViewReceipt: (PaymentReceipt) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ledger_card_${student.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = student.id,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = student.grade,
                            style = MaterialTheme.typography.labelMedium,
                            color = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${student.busRouteId.replace("ROUTE-", "Route ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.AM) student.amharicName else student.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${student.parentName} • ${student.parentPhone} • ${student.telegramHandle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                PaymentStatusBadge(status = student.paymentStatus, strings = strings)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LedgerStatColumn(strings.tuitionFee, formatEtb(student.tuitionFeeEtb, language))
                LedgerStatColumn(strings.busFee, formatEtb(student.busFeeEtb, language))
                LedgerStatColumn(strings.totalPaid, formatEtb(student.totalPaidEtb, language), EmeraldSecondary)
                LedgerStatColumn(
                    strings.balanceDue,
                    formatEtb(student.balanceDueEtb, language),
                    if (student.balanceDueEtb > 0) RoseError else EmeraldSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (student.balanceDueEtb > 0) {
                    Button(
                        onClick = onRecordPayment,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoBright),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("record_payment_${student.id}")
                    ) {
                        Icon(Icons.Filled.Payments, contentDescription = strings.payOrRecord, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.payOrRecord)
                    }
                }
                if (latestReceipt != null) {
                    OutlinedButton(
                        onClick = { onViewReceipt(latestReceipt) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("view_receipt_${student.id}")
                    ) {
                        Icon(Icons.Filled.ReceiptLong, contentDescription = strings.viewReceipt, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.viewReceipt)
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerStatColumn(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}

@Composable
private fun BulkReminderDialog(
    pendingStudents: List<Student>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSendAll: () -> Unit
) {
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
                    .padding(20.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.AM) "የSMS / Telegram የክፍያ ማሳሰቢያ ቅድመ እይታ" else "Bulk SMS & Telegram Fee Reminder Preview",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (language == AppLanguage.AM) {
                        "${pendingStudents.size} ወላጆች ያልተከፈለ ቀሪ ሂሳብ አላቸው"
                    } else {
                        "${pendingStudents.size} parent accounts with pending Term II balances"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                pendingStudents.take(5).forEach { s ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${s.parentName} (${s.telegramHandle})",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = formatEtb(s.balanceDueEtb, language),
                                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = MonoFontFamily),
                                    color = RoseError
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.AM) {
                                    "ውድ ${s.parentName}፣ የ${s.amharicName} (${s.id}) የ2ኛ ሩብ ዓመት ቀሪ ክፍያ ${s.balanceDueEtb} ብር በቴሌብር ወይም ሲቢኢ ብር ይክፈሉ።"
                                } else {
                                    "Dear ${s.parentName}, please settle ${s.fullName}'s (${s.id}) Term II balance of ${s.balanceDueEtb} ETB via Telebirr/CBE Birr."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (pendingStudents.size > 5) {
                    Text(
                        text = "+ ${pendingStudents.size - 5} more queued recipients...",
                        style = MaterialTheme.typography.labelMedium,
                        color = IndigoPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
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
                        onClick = onSendAll,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberTertiary),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("confirm_send_bulk_reminders")
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send Reminders", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.AM) "ሁሉንም ላክ" else "Send All (${pendingStudents.size})")
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordBankReceiptDialog(
    initialStudent: Student,
    allStudents: List<Student>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (studentId: String, amountEtb: Int, method: PaymentMethod, ref: String) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(initialStudent) }
    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var amountText by remember(selectedStudent.id) {
        mutableStateOf(selectedStudent.balanceDueEtb.coerceAtLeast(1000).toString())
    }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER) }
    var refNumber by remember { mutableStateOf("FT263298841") }

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
                    text = if (language == AppLanguage.AM) "የባንክ / የእጅ ደረሰኝ መዝግብ" else "Record Manual / Bank Receipt",
                    style = MaterialTheme.typography.titleLarge
                )

                // Student Picker
                Box {
                    OutlinedTextField(
                        value = "${selectedStudent.id} • ${selectedStudent.fullName} (Due: ${selectedStudent.balanceDueEtb} ETB)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (language == AppLanguage.AM) "ተማሪ ይምረጡ" else "Select Student") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { studentDropdownExpanded = true }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { studentDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = studentDropdownExpanded,
                        onDismissRequest = { studentDropdownExpanded = false }
                    ) {
                        allStudents.filter { it.balanceDueEtb > 0 }.ifEmpty { allStudents }.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.id} • ${s.fullName} (${s.balanceDueEtb} ETB)") },
                                onClick = {
                                    selectedStudent = s
                                    studentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Payment Method Chips
                Text(
                    text = if (language == AppLanguage.AM) "የክፍያ ዘዴ" else "Payment Channel",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        PaymentMethod.BANK_TRANSFER,
                        PaymentMethod.TELEBIRR,
                        PaymentMethod.CBE_BIRR,
                        PaymentMethod.CASH
                    ).forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(if (language == AppLanguage.AM) method.amharicName else method.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(if (language == AppLanguage.AM) "የተከፈለ መጠን (ብር)" else "Amount Paid (ETB)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_receipt_amount_input")
                )

                OutlinedTextField(
                    value = refNumber,
                    onValueChange = { refNumber = it },
                    label = { Text(if (language == AppLanguage.AM) "የባንክ ማጣቀሻ ቁጥር (Ref No.)" else "Bank Transfer Reference No.") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_receipt_ref_input")
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
                            val amt = amountText.toIntOrNull() ?: 0
                            if (amt > 0) {
                                onConfirm(selectedStudent.id, amt, selectedMethod, refNumber)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("confirm_manual_receipt_button")
                    ) {
                        Text(if (language == AppLanguage.AM) "አረጋግጥና መዝግብ" else "Verify & Post")
                    }
                }
            }
        }
    }
}
