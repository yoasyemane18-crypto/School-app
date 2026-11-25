package com.example.model

enum class AppRole {
    ADMIN,
    BUS_ATTENDANT,
    PARENT
}

enum class AppLanguage {
    EN,
    AM
}

enum class PaymentStatus {
    PAID,
    PARTIAL,
    OVERDUE
}

enum class TripMode {
    MORNING_PICKUP,
    AFTERNOON_DROPOFF
}

enum class BoardingStatus {
    WAITING,
    BOARDED,
    DROPPED_OFF,
    ABSENT
}

enum class PaymentMethod(val displayName: String, val amharicName: String) {
    TELEBIRR("Telebirr", "ቴሌብር"),
    CHAPA("Chapa", "ቻፓ"),
    CBE_BIRR("CBE Birr", "ሲቢኢ ብር"),
    BANK_TRANSFER("CBE / Bank Transfer", "የባንክ ዝውውር"),
    CASH("Cash at Bursar", "በጥሬ ገንዘብ")
}

data class Student(
    val id: String,
    val fullName: String,
    val amharicName: String,
    val grade: String,
    val parentId: String,
    val parentName: String,
    val parentPhone: String,
    val telegramHandle: String,
    val busRouteId: String,
    val pickupStopName: String,
    val tuitionFeeEtb: Int,
    val busFeeEtb: Int,
    val totalPaidEtb: Int,
    val boardingStatus: BoardingStatus = BoardingStatus.WAITING,
    val lastBoardingTimestamp: String = "07:10 AM",
    val lastBoardingNote: String = "Scheduled for Morning Pickup"
) {
    val totalFeeEtb: Int get() = tuitionFeeEtb + busFeeEtb
    val balanceDueEtb: Int get() = (totalFeeEtb - totalPaidEtb).coerceAtLeast(0)
    val paymentStatus: PaymentStatus
        get() = when {
            balanceDueEtb <= 0 -> PaymentStatus.PAID
            totalPaidEtb > 0 -> PaymentStatus.PARTIAL
            else -> PaymentStatus.OVERDUE
        }
}

data class BusStop(
    val id: String,
    val nameEn: String,
    val nameAm: String,
    val scheduledTimeMorning: String,
    val scheduledTimeAfternoon: String
)

data class BusRoute(
    val id: String,
    val code: String,
    val nameEn: String,
    val nameAm: String,
    val busPlate: String,
    val driverName: String,
    val driverPhone: String,
    val attendantName: String,
    val attendantPhone: String,
    val stops: List<BusStop>,
    val isLiveBroadcasting: Boolean = false,
    val tripMode: TripMode = TripMode.MORNING_PICKUP,
    val currentStopIndex: Int = 0,
    val progressFraction: Float = 0.0f,
    val etaMinutes: Int = 18
)

data class ParentProfile(
    val id: String,
    val fullName: String,
    val amharicName: String,
    val phone: String,
    val telegramHandle: String,
    val primaryRouteId: String
)

data class PaymentReceipt(
    val receiptNumber: String,
    val studentId: String,
    val studentName: String,
    val grade: String,
    val parentName: String,
    val tuitionPortionEtb: Int,
    val busPortionEtb: Int,
    val amountPaidEtb: Int,
    val method: PaymentMethod,
    val referenceNumber: String,
    val timestamp: String,
    val verifiedBy: String
)

data class SafetyNotification(
    val id: String,
    val studentId: String,
    val studentName: String,
    val parentId: String,
    val busCode: String,
    val titleEn: String,
    val titleAm: String,
    val detailEn: String,
    val detailAm: String,
    val timestamp: String,
    val type: BoardingStatus
)

data class PickupPass(
    val id: String,
    val code: String,
    val studentId: String,
    val studentName: String,
    val parentId: String,
    val delegateName: String,
    val relationship: String,
    val delegatePhone: String,
    val validDate: String,
    val isVerified: Boolean = false,
    val verifiedAt: String? = null
)

data class MonthlyCollectionStat(
    val monthEn: String,
    val monthAm: String,
    val collectedEtb: Int,
    val targetEtb: Int
)
