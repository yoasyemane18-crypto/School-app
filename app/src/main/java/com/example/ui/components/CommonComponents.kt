package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.BoardingStatus
import com.example.model.PaymentReceipt
import com.example.model.PaymentStatus
import com.example.ui.theme.AmberBus
import com.example.ui.theme.AmberContainerLight
import com.example.ui.theme.AmberOnContainerLight
import com.example.ui.theme.EmeraldContainerLight
import com.example.ui.theme.EmeraldOnContainerLight
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoBright
import com.example.ui.theme.IndigoContainerLight
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.IndigoOnContainerLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.RoseContainerLight
import com.example.ui.theme.RoseOnContainerLight
import java.text.NumberFormat
import java.util.Locale

fun formatEtb(amount: Int, language: AppLanguage): String {
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(amount)
    return if (language == AppLanguage.AM) "$formatted ብር" else "$formatted ETB"
}

@Composable
fun TopBrandAndRoleHeader(
    strings: AppStrings,
    activeRole: AppRole,
    language: AppLanguage,
    isDriverLocked: Boolean = false,
    isParentLocked: Boolean = false,
    onSelectRole: (AppRole) -> Unit,
    onToggleLanguage: () -> Unit,
    onResetDemo: () -> Unit
) {
    Surface(
        shadowElevation = 6.dp,
        color = IndigoDeep
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(IndigoDeep, IndigoPrimary)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_saferide_emblem),
                        contentDescription = "SafeRide & EduPay Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.appTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AmberBus.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = strings.termBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFDE68A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = strings.schoolName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = onToggleLanguage,
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                        modifier = Modifier
                            .testTag("language_toggle_button")
                            .heightIn(min = 40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "Toggle English or Amharic",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.EN) "EN / አማ" else "አማ / EN",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onResetDemo,
                        modifier = Modifier
                            .testTag("reset_demo_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = strings.resetDemo,
                            tint = Color(0xFFFDE68A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Switcher Bar
            Surface(
                color = Color.Black.copy(alpha = 0.24f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RoleTabChip(
                        label = strings.roleAdmin,
                        icon = Icons.Filled.AccountBalance,
                        selected = activeRole == AppRole.ADMIN,
                        onClick = { onSelectRole(AppRole.ADMIN) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_tab_admin")
                    )
                    RoleTabChip(
                        label = strings.roleBus,
                        icon = Icons.Filled.DirectionsBus,
                        selected = activeRole == AppRole.BUS_ATTENDANT,
                        showLockIcon = isDriverLocked,
                        onClick = { onSelectRole(AppRole.BUS_ATTENDANT) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_tab_bus")
                    )
                    RoleTabChip(
                        label = strings.roleParent,
                        icon = Icons.Filled.FamilyRestroom,
                        selected = activeRole == AppRole.PARENT,
                        showLockIcon = isParentLocked,
                        onClick = { onSelectRole(AppRole.PARENT) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_tab_parent")
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleTabChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    showLockIcon: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Transparent,
        label = "role_tab_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) IndigoDeep else Color.White.copy(alpha = 0.85f),
        label = "role_tab_fg"
    )

    Surface(
        onClick = onClick,
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.heightIn(min = 44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showLockIcon) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = if (selected) AmberBus else Color(0xFFFDE68A),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentStatusBadge(
    status: PaymentStatus,
    strings: AppStrings
) {
    val (bg, fg, text) = when (status) {
        PaymentStatus.PAID -> Triple(EmeraldContainerLight, EmeraldOnContainerLight, strings.statusPaid)
        PaymentStatus.PARTIAL -> Triple(AmberContainerLight, AmberOnContainerLight, strings.statusPartial)
        PaymentStatus.OVERDUE -> Triple(RoseContainerLight, RoseOnContainerLight, strings.statusOverdue)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BoardingStatusBadge(
    status: BoardingStatus,
    strings: AppStrings
) {
    val (bg, fg, text) = when (status) {
        BoardingStatus.WAITING -> Triple(IndigoContainerLight, IndigoOnContainerLight, strings.statusWaiting)
        BoardingStatus.BOARDED -> Triple(AmberContainerLight, AmberOnContainerLight, strings.statusBoarded)
        BoardingStatus.DROPPED_OFF -> Triple(EmeraldContainerLight, EmeraldOnContainerLight, strings.statusDroppedOff)
        BoardingStatus.ABSENT -> Triple(RoseContainerLight, RoseOnContainerLight, strings.statusAbsent)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun QrCodeMatrixCanvas(
    payload: String,
    sizeDp: Dp = 116.dp,
    tintColor: Color = IndigoDeep
) {
    val gridCount = 21
    Box(
        modifier = Modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.5.dp, tintColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cellSize = size.width / gridCount
            val seed = payload.hashCode()

            fun drawFinder(startCol: Int, startRow: Int) {
                for (r in 0..6) {
                    for (c in 0..6) {
                        val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                        val isCenter = r in 2..4 && c in 2..4
                        if (isBorder || isCenter) {
                            drawRect(
                                color = tintColor,
                                topLeft = Offset((startCol + c) * cellSize, (startRow + r) * cellSize),
                                size = Size(cellSize, cellSize)
                            )
                        }
                    }
                }
            }

            drawFinder(0, 0)
            drawFinder(gridCount - 7, 0)
            drawFinder(0, gridCount - 7)

            for (r in 0 until gridCount) {
                for (c in 0 until gridCount) {
                    val inTopLeft = r < 8 && c < 8
                    val inTopRight = r < 8 && c >= gridCount - 8
                    val inBottomLeft = r >= gridCount - 8 && c < 8
                    if (!inTopLeft && !inTopRight && !inBottomLeft) {
                        val hash = (seed * 31 + r * 17 + c * 13 + (r xor c) * 7)
                        if ((hash and 1) == 0 || (hash % 5 == 0)) {
                            drawRect(
                                color = tintColor,
                                topLeft = Offset(c * cellSize, r * cellSize),
                                size = Size(cellSize * 0.92f, cellSize * 0.92f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfficialReceiptDialog(
    receipt: PaymentReceipt,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainerLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified Receipt",
                                tint = EmeraldSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.AM) "የትምህርት ቤት ኦፊሴላዊ ደረሰኝ" else "OFFICIAL SCHOOL RECEIPT",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldSecondary
                            )
                            Text(
                                text = "Ethio-Horizon Academy",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Surface(
                        color = IndigoContainerLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = receipt.receiptNumber,
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = MonoFontFamily),
                            color = IndigoOnContainerLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(14.dp))

                ReceiptRow(
                    label = if (language == AppLanguage.AM) "ተማሪ / ክፍል" else "Student / Grade",
                    value = "${receipt.studentName} (${receipt.grade})"
                )
                ReceiptRow(
                    label = if (language == AppLanguage.AM) "የተማሪ መታወቂያ" else "Student ID",
                    value = receipt.studentId,
                    mono = true
                )
                ReceiptRow(
                    label = if (language == AppLanguage.AM) "ወላጅ" else "Parent / Guardian",
                    value = receipt.parentName
                )
                ReceiptRow(
                    label = if (language == AppLanguage.AM) "የክፍያ ዘዴ" else "Payment Method",
                    value = if (language == AppLanguage.AM) receipt.method.amharicName else receipt.method.displayName
                )
                ReceiptRow(
                    label = if (language == AppLanguage.AM) "የማጣቀሻ ቁጥር" else "Transaction Ref",
                    value = receipt.referenceNumber,
                    mono = true
                )
                ReceiptRow(
                    label = if (language == AppLanguage.AM) "ቀን እና ሰዓት" else "Date & Time",
                    value = receipt.timestamp
                )

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ReceiptRow(
                            label = if (language == AppLanguage.AM) "የትምህርት ክፍያ" else "Tuition Portion",
                            value = formatEtb(receipt.tuitionPortionEtb, language),
                            mono = true
                        )
                        ReceiptRow(
                            label = if (language == AppLanguage.AM) "የአውቶቡስ ክፍያ" else "Bus Transport Portion",
                            value = formatEtb(receipt.busPortionEtb, language),
                            mono = true
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.AM) "ጠቅላላ የተከፈለ" else "TOTAL PAID",
                                style = MaterialTheme.typography.titleSmall,
                                color = IndigoPrimary
                            )
                            Text(
                                text = formatEtb(receipt.amountPaidEtb, language),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = EmeraldSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QrCodeMatrixCanvas(payload = "${receipt.receiptNumber}|${receipt.referenceNumber}", sizeDp = 82.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified",
                                tint = EmeraldSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = receipt.verifiedBy,
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.AM) {
                                "ይህ ዲጂታል ደረሰኝ በኢትዮ-ሆራይዘን አካዳሚ ፋይናንስ ሲስተም የተረጋገጠ ነው።"
                            } else {
                                "Digitally signed & reconciled by Ethio-Horizon Academy Bursar System."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareBody = buildString {
                                appendLine("ETHIO-HORIZON ACADEMY — OFFICIAL RECEIPT")
                                appendLine("Receipt: ${receipt.receiptNumber}")
                                appendLine("Student: ${receipt.studentName} (${receipt.studentId}, ${receipt.grade})")
                                appendLine("Amount Paid: ${receipt.amountPaidEtb} ETB")
                                appendLine("Method: ${receipt.method.displayName} (Ref: ${receipt.referenceNumber})")
                                appendLine("Date: ${receipt.timestamp}")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "School Fee Receipt ${receipt.receiptNumber}")
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share / Print Receipt"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("share_receipt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Print,
                            contentDescription = "Share or Print Receipt",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.AM) "አጋራ / አትም" else "Share / Print")
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoBright),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("close_receipt_button")
                    ) {
                        Text(if (language == AppLanguage.AM) "ዝጋ" else "Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, mono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (mono) {
                MaterialTheme.typography.bodyMedium.copy(fontFamily = MonoFontFamily, fontWeight = FontWeight.SemiBold)
            } else {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            },
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
