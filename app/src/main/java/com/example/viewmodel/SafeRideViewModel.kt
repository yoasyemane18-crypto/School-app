package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DemoSeedData
import com.example.data.PersistedAppState
import com.example.data.SafeRideRepository
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.BoardingStatus
import com.example.model.BusRoute
import com.example.model.MonthlyCollectionStat
import com.example.model.ParentProfile
import com.example.model.PaymentMethod
import com.example.model.PaymentReceipt
import com.example.model.PaymentStatus
import com.example.model.PickupPass
import com.example.model.SafetyNotification
import com.example.model.Student
import com.example.model.TripMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class SafeRideUiState(
    val activeRole: AppRole = AppRole.ADMIN,
    val language: AppLanguage = AppLanguage.EN,
    val students: List<Student> = emptyList(),
    val routes: List<BusRoute> = emptyList(),
    val receipts: List<PaymentReceipt> = emptyList(),
    val notifications: List<SafetyNotification> = emptyList(),
    val pickupPasses: List<PickupPass> = emptyList(),
    val parentProfiles: List<ParentProfile> = DemoSeedData.parentProfiles,
    val selectedParentId: String = DemoSeedData.parentProfiles.first().id,
    val selectedRouteId: String = "ROUTE-A",
    val monthlyStats: List<MonthlyCollectionStat> = DemoSeedData.monthlyCollectionStats,
    // Admin Filters
    val searchQuery: String = "",
    val statusFilter: PaymentStatus? = null,
    val gradeFilter: String? = null,
    val routeFilter: String? = null,
    // Transient Feedback
    val snackbarMessage: String? = null,
    val latestGeneratedReceipt: PaymentReceipt? = null,
    // In-memory Biometric / Credential Manager Session Locks
    val isDriverUnlocked: Boolean = false,
    val isParentUnlocked: Boolean = false,
    val driverAuthMethod: String = "Biometric Verified",
    val parentAuthMethod: String = "Biometric Verified"
) {
    val totalEnrolled: Int get() = students.size
    val totalCollectedEtb: Int get() = students.sumOf { it.totalPaidEtb }
    val totalPendingEtb: Int get() = students.sumOf { it.balanceDueEtb }
    val activeBusCount: Int get() = routes.count { it.isLiveBroadcasting }

    val selectedRoute: BusRoute
        get() = routes.firstOrNull { it.id == selectedRouteId } ?: routes.first()

    val selectedParent: ParentProfile
        get() = parentProfiles.firstOrNull { it.id == selectedParentId } ?: parentProfiles.first()

    val filteredStudents: List<Student>
        get() = students.filter { s ->
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                s.id.lowercase().contains(q) ||
                s.fullName.lowercase().contains(q) ||
                s.amharicName.contains(q) ||
                s.parentName.lowercase().contains(q) ||
                s.parentPhone.lowercase().contains(q)

            val matchesStatus = statusFilter == null || s.paymentStatus == statusFilter
            val matchesGrade = when (gradeFilter) {
                null -> true
                "KG" -> s.grade.startsWith("KG")
                "G1-4" -> s.grade in listOf("Grade 1", "Grade 2", "Grade 3", "Grade 4")
                "G5-8" -> s.grade in listOf("Grade 5", "Grade 6", "Grade 7", "Grade 8")
                else -> s.grade == gradeFilter
            }
            val matchesRoute = routeFilter == null || s.busRouteId == routeFilter

            matchesQuery && matchesStatus && matchesGrade && matchesRoute
        }
}

class SafeRideViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SafeRideRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(SafeRideUiState())
    val uiState: StateFlow<SafeRideUiState> = _uiState.asStateFlow()

    init {
        val loaded = repository.loadState()
        _uiState.value = SafeRideUiState(
            activeRole = loaded.activeRole,
            language = loaded.language,
            students = loaded.students,
            routes = loaded.routes,
            receipts = loaded.receipts,
            notifications = loaded.notifications,
            pickupPasses = loaded.pickupPasses,
            selectedParentId = loaded.selectedParentId,
            selectedRouteId = loaded.selectedRouteId
        )
        startLiveGpsTicker()
    }

    private fun startLiveGpsTicker() {
        viewModelScope.launch {
            while (true) {
                delay(2400L)
                var changed = false
                _uiState.update { state ->
                    val updatedRoutes = state.routes.map { route ->
                        if (!route.isLiveBroadcasting) {
                            route
                        } else {
                            changed = true
                            val nextProgress = route.progressFraction + 0.08f
                            if (nextProgress >= 1.0f) {
                                val nextStop = (route.currentStopIndex + 1) % route.stops.size
                                val remainingStops = (route.stops.size - 1 - nextStop).coerceAtLeast(1)
                                route.copy(
                                    currentStopIndex = nextStop,
                                    progressFraction = 0.05f,
                                    etaMinutes = if (nextStop == route.stops.size - 1) 1 else remainingStops * 4
                                )
                            } else {
                                val remainingStops = (route.stops.size - 1 - route.currentStopIndex).coerceAtLeast(0)
                                val eta = (remainingStops * 4 + ((1f - nextProgress) * 3).toInt()).coerceAtLeast(1)
                                route.copy(
                                    progressFraction = nextProgress,
                                    etaMinutes = eta
                                )
                            }
                        }
                    }
                    if (changed) state.copy(routes = updatedRoutes) else state
                }
                if (changed) {
                    persistCurrentState()
                }
            }
        }
    }

    fun switchRole(role: AppRole) {
        _uiState.update { it.copy(activeRole = role) }
        persistCurrentState()
    }

    fun toggleLanguage() {
        _uiState.update {
            val next = if (it.language == AppLanguage.EN) AppLanguage.AM else AppLanguage.EN
            it.copy(language = next)
        }
        persistCurrentState()
    }

    fun selectParentProfile(parentId: String) {
        _uiState.update { it.copy(selectedParentId = parentId) }
        persistCurrentState()
    }

    fun selectBusRoute(routeId: String) {
        _uiState.update { it.copy(selectedRouteId = routeId) }
        persistCurrentState()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setStatusFilter(status: PaymentStatus?) {
        _uiState.update { it.copy(statusFilter = status) }
    }

    fun setGradeFilter(gradeBucket: String?) {
        _uiState.update { it.copy(gradeFilter = gradeBucket) }
    }

    fun setRouteFilter(routeId: String?) {
        _uiState.update { it.copy(routeFilter = routeId) }
    }

    fun toggleTripMode(routeId: String, mode: TripMode) {
        _uiState.update { state ->
            val updated = state.routes.map { r ->
                if (r.id == routeId) r.copy(tripMode = mode, currentStopIndex = 0, progressFraction = 0.05f)
                else r
            }
            state.copy(routes = updated)
        }
        persistCurrentState()
    }

    fun toggleLiveGpsBroadcast(routeId: String) {
        _uiState.update { state ->
            val updated = state.routes.map { r ->
                if (r.id == routeId) r.copy(isLiveBroadcasting = !r.isLiveBroadcasting)
                else r
            }
            val targetRoute = updated.first { it.id == routeId }
            val msg = if (targetRoute.isLiveBroadcasting) {
                if (state.language == AppLanguage.AM) "${targetRoute.nameAm} የቀጥታ GPS ተጀምሯል"
                else "${targetRoute.code} Live GPS broadcast started"
            } else {
                if (state.language == AppLanguage.AM) "${targetRoute.nameAm} የቀጥታ GPS ቆሟል"
                else "${targetRoute.code} GPS broadcast paused"
            }
            state.copy(routes = updated, snackbarMessage = msg)
        }
        persistCurrentState()
    }

    fun advanceRouteStop(routeId: String) {
        _uiState.update { state ->
            val updated = state.routes.map { r ->
                if (r.id == routeId) {
                    val nextStop = (r.currentStopIndex + 1) % r.stops.size
                    val remaining = (r.stops.size - 1 - nextStop).coerceAtLeast(1)
                    r.copy(
                        currentStopIndex = nextStop,
                        progressFraction = 0.1f,
                        etaMinutes = if (nextStop == r.stops.size - 1) 1 else remaining * 4
                    )
                } else r
            }
            state.copy(routes = updated)
        }
        persistCurrentState()
    }

    fun markStudentBoarding(studentId: String, newStatus: BoardingStatus) {
        val timeNow = currentTimeShort()
        _uiState.update { state ->
            val route = state.routes.firstOrNull { r ->
                state.students.firstOrNull { it.id == studentId }?.busRouteId == r.id
            }
            val student = state.students.firstOrNull { it.id == studentId } ?: return@update state
            val currentStop = route?.stops?.getOrNull(route.currentStopIndex)

            val noteEn = when (newStatus) {
                BoardingStatus.BOARDED -> "Boarded ${route?.code ?: ""} at ${currentStop?.nameEn ?: student.pickupStopName}"
                BoardingStatus.DROPPED_OFF -> if (route?.tripMode == TripMode.MORNING_PICKUP) {
                    "Arrived safely at Ethio-Horizon Campus"
                } else {
                    "Dropped off safely at ${student.pickupStopName}"
                }
                BoardingStatus.ABSENT -> "Marked Absent Today by ${route?.attendantName ?: "Attendant"}"
                BoardingStatus.WAITING -> "Scheduled for Pickup"
            }

            val updatedStudents = state.students.map { s ->
                if (s.id == studentId) {
                    s.copy(
                        boardingStatus = newStatus,
                        lastBoardingTimestamp = timeNow,
                        lastBoardingNote = noteEn
                    )
                } else s
            }

            val (titleEn, titleAm, detailEn, detailAm) = when (newStatus) {
                BoardingStatus.BOARDED -> listOf(
                    "Boarded School Bus (${route?.code ?: ""})",
                    "ወደ ት/ቤት አውቶቡስ ተሳፍሯል (${route?.code ?: ""})",
                    "${student.fullName} boarded at ${currentStop?.nameEn ?: student.pickupStopName} with ${route?.attendantName ?: "Attendant"}.",
                    "${student.amharicName} በ${currentStop?.nameAm ?: student.pickupStopName} ከ${route?.attendantName ?: "ረዳት"} ጋር ተሳፍሯል።"
                )
                BoardingStatus.DROPPED_OFF -> listOf(
                    if (route?.tripMode == TripMode.MORNING_PICKUP) "Arrived Safely at School" else "Dropped Off Safely at Stop",
                    if (route?.tripMode == TripMode.MORNING_PICKUP) "ት/ቤት በሰላም ደርሷል" else "ቤት/ፌርማታ በሰላም ወርዷል",
                    "${student.fullName} checked out safely at $timeNow (${route?.code ?: ""}).",
                    "${student.amharicName} በ$timeNow ሰዓት በሰላም ወርዷል።"
                )
                BoardingStatus.ABSENT -> listOf(
                    "Marked Absent from Bus",
                    "ከአውቶቡስ ቀርቷል ተብሎ ተመዝግቧል",
                    "${student.fullName} did not board ${route?.code ?: ""} at ${student.pickupStopName}.",
                    "${student.amharicName} በ${student.pickupStopName} ፌርማታ አልተሳፈረም።"
                )
                BoardingStatus.WAITING -> listOf(
                    "Waiting for Pickup",
                    "በመጠበቅ ላይ",
                    "${student.fullName} status reset to waiting.",
                    "${student.amharicName} ሁኔታ ወደ መጠበቅ ተመልሷል።"
                )
            }

            val newNotif = SafetyNotification(
                id = "NOTIF-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}",
                studentId = student.id,
                studentName = student.fullName,
                parentId = student.parentId,
                busCode = route?.code ?: student.busRouteId,
                titleEn = titleEn,
                titleAm = titleAm,
                detailEn = detailEn,
                detailAm = detailAm,
                timestamp = timeNow,
                type = newStatus
            )

            val toast = if (state.language == AppLanguage.AM) {
                "${student.amharicName} • $titleAm ($timeNow)"
            } else {
                "${student.fullName}: $titleEn ($timeNow)"
            }

            state.copy(
                students = updatedStudents,
                notifications = listOf(newNotif) + state.notifications,
                snackbarMessage = toast
            )
        }
        persistCurrentState()
    }

    fun recordPayment(
        studentId: String,
        amountEtb: Int,
        method: PaymentMethod,
        referenceNumber: String,
        verifiedBy: String
    ): PaymentReceipt? {
        if (amountEtb <= 0) return null
        var createdReceipt: PaymentReceipt? = null
        val timestamp = currentDateTimeFormatted()
        val receiptNo = "RCP-2026-${Random.nextInt(8900, 9999)}"

        _uiState.update { state ->
            val target = state.students.firstOrNull { it.id == studentId } ?: return@update state
            val appliedAmount = amountEtb.coerceAtMost(target.balanceDueEtb.coerceAtLeast(amountEtb))
            val newPaid = (target.totalPaidEtb + appliedAmount).coerceAtMost(target.totalFeeEtb)
            val actualDelta = newPaid - target.totalPaidEtb
            val finalAmount = if (actualDelta > 0) actualDelta else appliedAmount

            val tuitionShare = (finalAmount * 0.78).toInt()
            val busShare = (finalAmount - tuitionShare).coerceAtLeast(0)

            val receipt = PaymentReceipt(
                receiptNumber = receiptNo,
                studentId = target.id,
                studentName = target.fullName,
                grade = target.grade,
                parentName = target.parentName,
                tuitionPortionEtb = tuitionShare,
                busPortionEtb = busShare,
                amountPaidEtb = finalAmount,
                method = method,
                referenceNumber = referenceNumber.ifBlank { "REF-${Random.nextInt(100000, 999999)}" },
                timestamp = timestamp,
                verifiedBy = verifiedBy
            )
            createdReceipt = receipt

            val updatedStudents = state.students.map { s ->
                if (s.id == studentId) s.copy(totalPaidEtb = newPaid) else s
            }

            val updatedMonthly = state.monthlyStats.mapIndexed { idx, stat ->
                if (idx == state.monthlyStats.lastIndex) {
                    stat.copy(collectedEtb = stat.collectedEtb + finalAmount)
                } else stat
            }

            val msg = if (state.language == AppLanguage.AM) {
                "ክፍያ ተረጋግጧል፡ ${target.amharicName} (${finalAmount} ብር)"
            } else {
                "Payment verified for ${target.fullName} ($finalAmount ETB)"
            }

            state.copy(
                students = updatedStudents,
                receipts = listOf(receipt) + state.receipts,
                monthlyStats = updatedMonthly,
                latestGeneratedReceipt = receipt,
                snackbarMessage = msg
            )
        }
        persistCurrentState()
        return createdReceipt
    }

    fun payParentTotalBalance(parentId: String, method: PaymentMethod, phoneOrAccount: String): PaymentReceipt? {
        val childrenWithBalance = _uiState.value.students.filter {
            it.parentId == parentId && it.balanceDueEtb > 0
        }
        if (childrenWithBalance.isEmpty()) return null

        var lastReceipt: PaymentReceipt? = null
        val prefix = when (method) {
            PaymentMethod.TELEBIRR -> "TLB"
            PaymentMethod.CHAPA -> "CHP"
            PaymentMethod.CBE_BIRR -> "CBE"
            else -> "BNK"
        }
        val refBase = "$prefix${Random.nextInt(10000000, 99999999)}"

        childrenWithBalance.forEachIndexed { idx, child ->
            val ref = if (idx == 0) refBase else "$refBase-${idx + 1}"
            lastReceipt = recordPayment(
                studentId = child.id,
                amountEtb = child.balanceDueEtb,
                method = method,
                referenceNumber = ref,
                verifiedBy = "Auto-Verified (${method.displayName} • $phoneOrAccount)"
            )
        }
        return lastReceipt
    }

    fun generatePickupPass(
        studentId: String,
        delegateName: String,
        relationship: String,
        delegatePhone: String
    ): PickupPass? {
        val student = _uiState.value.students.firstOrNull { it.id == studentId } ?: return null
        if (delegateName.isBlank()) return null

        val code = "SR-${Random.nextInt(1000, 9999)}"
        val pass = PickupPass(
            id = "PASS-${System.currentTimeMillis()}",
            code = code,
            studentId = student.id,
            studentName = student.fullName,
            parentId = student.parentId,
            delegateName = delegateName.trim(),
            relationship = relationship.ifBlank { "Family Delegate" },
            delegatePhone = delegatePhone.ifBlank { student.parentPhone },
            validDate = "Today • Afternoon Drop-off",
            isVerified = false
        )

        _uiState.update { state ->
            val msg = if (state.language == AppLanguage.AM) {
                "የመውሰጃ ፈቃድ $code ለ${student.amharicName} ተፈጥሯል"
            } else {
                "Authorized Pickup Pass $code created for ${student.fullName}"
            }
            state.copy(
                pickupPasses = listOf(pass) + state.pickupPasses,
                snackbarMessage = msg
            )
        }
        persistCurrentState()
        return pass
    }

    fun verifyPickupPassByCode(code: String): PickupPass? {
        val normalized = code.trim().uppercase()
        val matched = _uiState.value.pickupPasses.firstOrNull { it.code.uppercase() == normalized }
            ?: return null
        val timeNow = currentTimeShort()

        _uiState.update { state ->
            val updatedPasses = state.pickupPasses.map { p ->
                if (p.id == matched.id) p.copy(isVerified = true, verifiedAt = timeNow) else p
            }
            val student = state.students.firstOrNull { it.id == matched.studentId }
            val updatedStudents = state.students.map { s ->
                if (s.id == matched.studentId) {
                    s.copy(
                        boardingStatus = BoardingStatus.DROPPED_OFF,
                        lastBoardingTimestamp = timeNow,
                        lastBoardingNote = "Handed off via QR (${matched.code}) to ${matched.delegateName}"
                    )
                } else s
            }
            val notif = SafetyNotification(
                id = "NOTIF-${System.currentTimeMillis()}",
                studentId = matched.studentId,
                studentName = matched.studentName,
                parentId = matched.parentId,
                busCode = student?.busRouteId ?: "BUS",
                titleEn = "Authorized QR Handoff Complete",
                titleAm = "በQR ፈቃድ ልጅ ተረክቧል",
                detailEn = "${matched.studentName} handed safely to ${matched.delegateName} (${matched.relationship}) using pass ${matched.code}.",
                detailAm = "${matched.studentName} በፈቃድ ቁጥር ${matched.code} ለ${matched.delegateName} (${matched.relationship}) በሰላም ተሰጥቷል።",
                timestamp = timeNow,
                type = BoardingStatus.DROPPED_OFF
            )
            val msg = if (state.language == AppLanguage.AM) {
                "QR ኮድ ${matched.code} ተረጋግጧል — ለ${matched.delegateName} ተሰጥቷል"
            } else {
                "Verified Pass ${matched.code} — Safe handoff to ${matched.delegateName}"
            }
            state.copy(
                pickupPasses = updatedPasses,
                students = updatedStudents,
                notifications = listOf(notif) + state.notifications,
                snackbarMessage = msg
            )
        }
        persistCurrentState()
        return _uiState.value.pickupPasses.firstOrNull { it.id == matched.id }
    }

    fun dispatchBulkReminders(count: Int) {
        _uiState.update { state ->
            val msg = if (state.language == AppLanguage.AM) {
                "ለ$count ወላጆች የSMS እና Telegram የክፍያ ማሳሰቢያ ተልኳል!"
            } else {
                "Bulk SMS & Telegram fee reminders dispatched to $count parents!"
            }
            state.copy(snackbarMessage = msg)
        }
    }

    fun showReceipt(receipt: PaymentReceipt?) {
        _uiState.update { it.copy(latestGeneratedReceipt = receipt) }
    }

    fun showMessage(msg: String) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun unlockPortal(role: AppRole, methodLabel: String) {
        _uiState.update { state ->
            when (role) {
                AppRole.BUS_ATTENDANT -> state.copy(
                    isDriverUnlocked = true,
                    driverAuthMethod = methodLabel,
                    snackbarMessage = if (state.language == AppLanguage.AM) {
                        "የአውቶቡስ መተግበሪያ ተከፍቷል ($methodLabel)"
                    } else {
                        "Driver portal unlocked ($methodLabel)"
                    }
                )
                AppRole.PARENT -> state.copy(
                    isParentUnlocked = true,
                    parentAuthMethod = methodLabel,
                    snackbarMessage = if (state.language == AppLanguage.AM) {
                        "የወላጅ ፖርታል ተከፍቷል ($methodLabel)"
                    } else {
                        "Parent portal unlocked ($methodLabel)"
                    }
                )
                AppRole.ADMIN -> state
            }
        }
    }

    fun lockPortal(role: AppRole) {
        _uiState.update { state ->
            when (role) {
                AppRole.BUS_ATTENDANT -> state.copy(
                    isDriverUnlocked = false,
                    snackbarMessage = if (state.language == AppLanguage.AM) "የአውቶቡስ መተግበሪያ ተቆልፏል" else "Driver portal locked"
                )
                AppRole.PARENT -> state.copy(
                    isParentUnlocked = false,
                    snackbarMessage = if (state.language == AppLanguage.AM) "የወላጅ ፖርታል ተቆልፏል" else "Parent portal locked"
                )
                AppRole.ADMIN -> state
            }
        }
    }

    fun resetDemoData() {
        val fresh = repository.resetDemoData()
        _uiState.update { current ->
            SafeRideUiState(
                activeRole = current.activeRole,
                language = current.language,
                students = fresh.students,
                routes = fresh.routes,
                receipts = fresh.receipts,
                notifications = fresh.notifications,
                pickupPasses = fresh.pickupPasses,
                selectedParentId = fresh.selectedParentId,
                selectedRouteId = fresh.selectedRouteId,
                snackbarMessage = if (current.language == AppLanguage.AM) {
                    "የማሳያ ዳታ ወደ መጀመሪያው ሁኔታ ተመልሷል"
                } else {
                    "Demo data reset to initial Term II state"
                }
            )
        }
    }

    private fun persistCurrentState() {
        val s = _uiState.value
        repository.saveState(
            PersistedAppState(
                students = s.students,
                routes = s.routes,
                receipts = s.receipts,
                notifications = s.notifications,
                pickupPasses = s.pickupPasses,
                activeRole = s.activeRole,
                language = s.language,
                selectedParentId = s.selectedParentId,
                selectedRouteId = s.selectedRouteId
            )
        )
    }

    private fun currentTimeShort(): String =
        SimpleDateFormat("hh:mm a", Locale.US).format(Date())

    private fun currentDateTimeFormatted(): String =
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US).format(Date())
}
