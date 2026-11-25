package com.example.data

import com.example.model.BoardingStatus
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.MonthlyCollectionStat
import com.example.model.ParentProfile
import com.example.model.PaymentMethod
import com.example.model.PaymentReceipt
import com.example.model.PickupPass
import com.example.model.SafetyNotification
import com.example.model.Student
import com.example.model.TripMode

object DemoSeedData {

    val parentProfiles: List<ParentProfile> = listOf(
        ParentProfile(
            id = "PAR-01",
            fullName = "Ato Bekele Tadesse",
            amharicName = "አቶ በቀለ ታደሰ",
            phone = "+251 911 234 567",
            telegramHandle = "@bekele_t",
            primaryRouteId = "ROUTE-A"
        ),
        ParentProfile(
            id = "PAR-02",
            fullName = "W/ro Marta Tesfaye",
            amharicName = "ወ/ሮ ማርታ ተስፋዬ",
            phone = "+251 912 448 910",
            telegramHandle = "@marta_tesfaye",
            primaryRouteId = "ROUTE-B"
        ),
        ParentProfile(
            id = "PAR-03",
            fullName = "Ato Girma Desta",
            amharicName = "አቶ ግርማ ደስታ",
            phone = "+251 913 772 305",
            telegramHandle = "@girma_d",
            primaryRouteId = "ROUTE-C"
        )
    )

    fun initialRoutes(): List<BusRoute> = listOf(
        BusRoute(
            id = "ROUTE-A",
            code = "Route A",
            nameEn = "Route A: Bole - CMC",
            nameAm = "መስመር A፡ ቦሌ - ሲኤምሲ",
            busPlate = "AA-3-40128 (Bus ET-401)",
            driverName = "Ato Tesfaye Lemma",
            driverPhone = "+251 911 804 112",
            attendantName = "W/ro Tigist Mamo",
            attendantPhone = "+251 911 804 115",
            isLiveBroadcasting = true,
            tripMode = TripMode.MORNING_PICKUP,
            currentStopIndex = 2,
            progressFraction = 0.45f,
            etaMinutes = 11,
            stops = listOf(
                BusStop("A1", "Bole Medhanialem", "ቦሌ መድኃኔዓለም", "06:55 AM", "03:40 PM"),
                BusStop("A2", "Atlas Junction", "አትላስ መስቀለኛ", "07:05 AM", "03:52 PM"),
                BusStop("A3", "Hayahulet Mazoria", "ሃያ ሁለት ማዞሪያ", "07:15 AM", "04:05 PM"),
                BusStop("A4", "Megenagna Square", "መገናኛ አደባባይ", "07:25 AM", "04:18 PM"),
                BusStop("A5", "Summit Condo", "ሰሚት ኮንዶሚኒየም", "07:35 AM", "04:30 PM"),
                BusStop("A6", "Ethio-Horizon Campus", "ኢትዮ-ሆራይዘን ት/ቤት", "07:45 AM", "03:30 PM")
            )
        ),
        BusRoute(
            id = "ROUTE-B",
            code = "Route B",
            nameEn = "Route B: Kazanchis - Gerji",
            nameAm = "መስመር B፡ ካዛንቺስ - ገርጂ",
            busPlate = "AA-3-51904 (Bus ET-402)",
            driverName = "Ato Girma Bekele",
            driverPhone = "+251 912 609 221",
            attendantName = "W/ro Genet Kassahun",
            attendantPhone = "+251 912 609 224",
            isLiveBroadcasting = false,
            tripMode = TripMode.MORNING_PICKUP,
            currentStopIndex = 1,
            progressFraction = 0.25f,
            etaMinutes = 16,
            stops = listOf(
                BusStop("B1", "Kazanchis Total", "ካዛንቺስ ቶታል", "06:50 AM", "03:40 PM"),
                BusStop("B2", "Urael Church", "ኡራኤል ቤተክርስቲያን", "07:02 AM", "03:55 PM"),
                BusStop("B3", "Bole Imperial", "ቦሌ ኢምፔሪያል", "07:14 AM", "04:08 PM"),
                BusStop("B4", "Gerji Roba Bakery", "ገርጂ ሮባ ዳቦ ቤት", "07:28 AM", "04:22 PM"),
                BusStop("B5", "Ethio-Horizon Campus", "ኢትዮ-ሆራይዘን ት/ቤት", "07:44 AM", "03:30 PM")
            )
        ),
        BusRoute(
            id = "ROUTE-C",
            code = "Route C",
            nameEn = "Route C: Sarbet - Bisrate Gabriel",
            nameAm = "መስመር C፡ ሳርቤት - ብስራተ ገብርኤል",
            busPlate = "AA-3-62077 (Bus ET-403)",
            driverName = "Ato Solomon Tilahun",
            driverPhone = "+251 913 310 882",
            attendantName = "W/ro Aster Kebede",
            attendantPhone = "+251 913 310 885",
            isLiveBroadcasting = false,
            tripMode = TripMode.MORNING_PICKUP,
            currentStopIndex = 0,
            progressFraction = 0.1f,
            etaMinutes = 22,
            stops = listOf(
                BusStop("C1", "Sarbet Adams Pavilion", "ሳርቤት አዳምስ ፓቪሊዮን", "06:50 AM", "03:45 PM"),
                BusStop("C2", "Bisrate Gabriel", "ብስራተ ገብርኤል", "07:04 AM", "03:58 PM"),
                BusStop("C3", "Mechare Meda", "መቻሬ ሜዳ", "07:16 AM", "04:10 PM"),
                BusStop("C4", "Lideta Condos", "ልደታ ኮንዶሚኒየም", "07:29 AM", "04:22 PM"),
                BusStop("C5", "Ethio-Horizon Campus", "ኢትዮ-ሆራይዘን ት/ቤት", "07:45 AM", "03:30 PM")
            )
        )
    )

    fun initialStudents(): List<Student> = listOf(
        // Family 1: Ato Bekele Tadesse (Dawit & Hanna)
        Student(
            id = "EH-1001",
            fullName = "Dawit Bekele",
            amharicName = "ዳዊት በቀለ",
            grade = "Grade 5",
            parentId = "PAR-01",
            parentName = "Ato Bekele Tadesse",
            parentPhone = "+251 911 234 567",
            telegramHandle = "@bekele_t",
            busRouteId = "ROUTE-A",
            pickupStopName = "Bole Medhanialem",
            tuitionFeeEtb = 14500,
            busFeeEtb = 3800,
            totalPaidEtb = 10000,
            boardingStatus = BoardingStatus.BOARDED,
            lastBoardingTimestamp = "07:02 AM",
            lastBoardingNote = "Boarded at Bole Medhanialem"
        ),
        Student(
            id = "EH-1002",
            fullName = "Hanna Bekele",
            amharicName = "ሐና በቀለ",
            grade = "Grade 2",
            parentId = "PAR-01",
            parentName = "Ato Bekele Tadesse",
            parentPhone = "+251 911 234 567",
            telegramHandle = "@bekele_t",
            busRouteId = "ROUTE-A",
            pickupStopName = "Bole Medhanialem",
            tuitionFeeEtb = 12800,
            busFeeEtb = 3800,
            totalPaidEtb = 16600,
            boardingStatus = BoardingStatus.BOARDED,
            lastBoardingTimestamp = "07:02 AM",
            lastBoardingNote = "Boarded at Bole Medhanialem"
        ),
        // Family 2: W/ro Marta Tesfaye (Abebe & Selamawit)
        Student(
            id = "EH-1003",
            fullName = "Abebe Tesfaye",
            amharicName = "አበበ ተስፋዬ",
            grade = "Grade 7",
            parentId = "PAR-02",
            parentName = "W/ro Marta Tesfaye",
            parentPhone = "+251 912 448 910",
            telegramHandle = "@marta_tesfaye",
            busRouteId = "ROUTE-B",
            pickupStopName = "Kazanchis Total",
            tuitionFeeEtb = 16200,
            busFeeEtb = 3600,
            totalPaidEtb = 0,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "06:50 AM",
            lastBoardingNote = "Waiting at Kazanchis Total"
        ),
        Student(
            id = "EH-1004",
            fullName = "Selamawit Tesfaye",
            amharicName = "ሰላማዊት ተስፋዬ",
            grade = "KG-2",
            parentId = "PAR-02",
            parentName = "W/ro Marta Tesfaye",
            parentPhone = "+251 912 448 910",
            telegramHandle = "@marta_tesfaye",
            busRouteId = "ROUTE-B",
            pickupStopName = "Kazanchis Total",
            tuitionFeeEtb = 11500,
            busFeeEtb = 3600,
            totalPaidEtb = 15100,
            boardingStatus = BoardingStatus.BOARDED,
            lastBoardingTimestamp = "06:56 AM",
            lastBoardingNote = "Boarded at Kazanchis Total"
        ),
        // Family 3: Ato Girma Desta (Yohannes & Mekdes)
        Student(
            id = "EH-1005",
            fullName = "Yohannes Girma",
            amharicName = "ዮሐንስ ግርማ",
            grade = "Grade 8",
            parentId = "PAR-03",
            parentName = "Ato Girma Desta",
            parentPhone = "+251 913 772 305",
            telegramHandle = "@girma_d",
            busRouteId = "ROUTE-C",
            pickupStopName = "Sarbet Adams Pavilion",
            tuitionFeeEtb = 17000,
            busFeeEtb = 4000,
            totalPaidEtb = 12000,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "06:50 AM",
            lastBoardingNote = "Scheduled at Sarbet Adams Pavilion"
        ),
        Student(
            id = "EH-1006",
            fullName = "Mekdes Girma",
            amharicName = "መቅደስ ግርማ",
            grade = "Grade 4",
            parentId = "PAR-03",
            parentName = "Ato Girma Desta",
            parentPhone = "+251 913 772 305",
            telegramHandle = "@girma_d",
            busRouteId = "ROUTE-C",
            pickupStopName = "Sarbet Adams Pavilion",
            tuitionFeeEtb = 13800,
            busFeeEtb = 4000,
            totalPaidEtb = 17800,
            boardingStatus = BoardingStatus.DROPPED_OFF,
            lastBoardingTimestamp = "07:42 AM",
            lastBoardingNote = "Arrived Safely at School"
        ),
        // Additional 10 Students across KG-1 to Grade 8 & all 3 routes
        Student(
            id = "EH-1007",
            fullName = "Bereket Alemu",
            amharicName = "በረከት አለሙ",
            grade = "Grade 6",
            parentId = "PAR-04",
            parentName = "Ato Alemu Hailu",
            parentPhone = "+251 911 552 190",
            telegramHandle = "@alemu_h",
            busRouteId = "ROUTE-A",
            pickupStopName = "Atlas Junction",
            tuitionFeeEtb = 15200,
            busFeeEtb = 3800,
            totalPaidEtb = 19000,
            boardingStatus = BoardingStatus.BOARDED,
            lastBoardingTimestamp = "07:08 AM",
            lastBoardingNote = "Boarded at Atlas Junction"
        ),
        Student(
            id = "EH-1008",
            fullName = "Tigist Haile",
            amharicName = "ትግስት ኃይሌ",
            grade = "KG-1",
            parentId = "PAR-05",
            parentName = "W/ro Rahel Worku",
            parentPhone = "+251 920 118 432",
            telegramHandle = "@rahel_w",
            busRouteId = "ROUTE-A",
            pickupStopName = "Hayahulet Mazoria",
            tuitionFeeEtb = 11200,
            busFeeEtb = 3800,
            totalPaidEtb = 0,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:15 AM",
            lastBoardingNote = "Waiting at Hayahulet Mazoria"
        ),
        Student(
            id = "EH-1009",
            fullName = "Nahom Solomon",
            amharicName = "ናሆም ሰለሞን",
            grade = "Grade 3",
            parentId = "PAR-06",
            parentName = "Ato Solomon Kebede",
            parentPhone = "+251 914 902 771",
            telegramHandle = "@solomon_k",
            busRouteId = "ROUTE-A",
            pickupStopName = "Megenagna Square",
            tuitionFeeEtb = 13200,
            busFeeEtb = 3800,
            totalPaidEtb = 8500,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:25 AM",
            lastBoardingNote = "Waiting at Megenagna Square"
        ),
        Student(
            id = "EH-1010",
            fullName = "Lidya Mulugeta",
            amharicName = "ሊድያ ሙሉጌታ",
            grade = "Grade 8",
            parentId = "PAR-07",
            parentName = "Dr. Mulugeta Abebe",
            parentPhone = "+251 911 664 098",
            telegramHandle = "@drmulugeta",
            busRouteId = "ROUTE-A",
            pickupStopName = "Summit Condo",
            tuitionFeeEtb = 17000,
            busFeeEtb = 3800,
            totalPaidEtb = 20800,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:35 AM",
            lastBoardingNote = "Waiting at Summit Condo"
        ),
        Student(
            id = "EH-1011",
            fullName = "Samuel Getachew",
            amharicName = "ሳሙኤል ጌታቸው",
            grade = "Grade 1",
            parentId = "PAR-08",
            parentName = "W/ro Hirut Getachew",
            parentPhone = "+251 918 334 512",
            telegramHandle = "@hirut_g",
            busRouteId = "ROUTE-B",
            pickupStopName = "Urael Church",
            tuitionFeeEtb = 12200,
            busFeeEtb = 3600,
            totalPaidEtb = 15800,
            boardingStatus = BoardingStatus.BOARDED,
            lastBoardingTimestamp = "07:04 AM",
            lastBoardingNote = "Boarded at Urael Church"
        ),
        Student(
            id = "EH-1012",
            fullName = "Bethlehem Negash",
            amharicName = "ቤተልሔም ነጋሽ",
            grade = "Grade 5",
            parentId = "PAR-09",
            parentName = "Ato Negash Tsegaye",
            parentPhone = "+251 911 991 203",
            telegramHandle = "@negash_ts",
            busRouteId = "ROUTE-B",
            pickupStopName = "Bole Imperial",
            tuitionFeeEtb = 14500,
            busFeeEtb = 3600,
            totalPaidEtb = 9000,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:14 AM",
            lastBoardingNote = "Waiting at Bole Imperial"
        ),
        Student(
            id = "EH-1013",
            fullName = "Kidus Assefa",
            amharicName = "ቅዱስ አሰፋ",
            grade = "Grade 4",
            parentId = "PAR-10",
            parentName = "Ato Assefa Wolde",
            parentPhone = "+251 915 412 890",
            telegramHandle = "@assefa_w",
            busRouteId = "ROUTE-B",
            pickupStopName = "Gerji Roba Bakery",
            tuitionFeeEtb = 13800,
            busFeeEtb = 3600,
            totalPaidEtb = 0,
            boardingStatus = BoardingStatus.ABSENT,
            lastBoardingTimestamp = "07:00 AM",
            lastBoardingNote = "Marked Absent by Attendant"
        ),
        Student(
            id = "EH-1014",
            fullName = "Meron Fikadu",
            amharicName = "ሜሮን ፍቃዱ",
            grade = "KG-2",
            parentId = "PAR-11",
            parentName = "W/ro Meseret Fikadu",
            parentPhone = "+251 912 773 451",
            telegramHandle = "@meseret_f",
            busRouteId = "ROUTE-C",
            pickupStopName = "Bisrate Gabriel",
            tuitionFeeEtb = 11500,
            busFeeEtb = 4000,
            totalPaidEtb = 15500,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:04 AM",
            lastBoardingNote = "Waiting at Bisrate Gabriel"
        ),
        Student(
            id = "EH-1015",
            fullName = "Eyasu Tadesse",
            amharicName = "ኢያሱ ታደሰ",
            grade = "Grade 6",
            parentId = "PAR-12",
            parentName = "Ato Tadesse Mekonnen",
            parentPhone = "+251 911 329 844",
            telegramHandle = "@tadesse_m",
            busRouteId = "ROUTE-C",
            pickupStopName = "Mechare Meda",
            tuitionFeeEtb = 15200,
            busFeeEtb = 4000,
            totalPaidEtb = 10000,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:16 AM",
            lastBoardingNote = "Waiting at Mechare Meda"
        ),
        Student(
            id = "EH-1016",
            fullName = "Feven Birhanu",
            amharicName = "ፌቨን ብርሃኑ",
            grade = "Grade 7",
            parentId = "PAR-13",
            parentName = "W/ro Aster Birhanu",
            parentPhone = "+251 916 501 229",
            telegramHandle = "@aster_b",
            busRouteId = "ROUTE-C",
            pickupStopName = "Lideta Condos",
            tuitionFeeEtb = 16200,
            busFeeEtb = 4000,
            totalPaidEtb = 0,
            boardingStatus = BoardingStatus.WAITING,
            lastBoardingTimestamp = "07:29 AM",
            lastBoardingNote = "Waiting at Lideta Condos"
        )
    )

    fun initialReceipts(): List<PaymentReceipt> = listOf(
        PaymentReceipt(
            receiptNumber = "RCP-2026-8841",
            studentId = "EH-1002",
            studentName = "Hanna Bekele",
            grade = "Grade 2",
            parentName = "Ato Bekele Tadesse",
            tuitionPortionEtb = 12800,
            busPortionEtb = 3800,
            amountPaidEtb = 16600,
            method = PaymentMethod.TELEBIRR,
            referenceNumber = "TLB99284105",
            timestamp = "Nov 18, 2026 • 09:14 AM",
            verifiedBy = "Auto-Verified (Telebirr)"
        ),
        PaymentReceipt(
            receiptNumber = "RCP-2026-8835",
            studentId = "EH-1001",
            studentName = "Dawit Bekele",
            grade = "Grade 5",
            parentName = "Ato Bekele Tadesse",
            tuitionPortionEtb = 10000,
            busPortionEtb = 0,
            amountPaidEtb = 10000,
            method = PaymentMethod.CBE_BIRR,
            referenceNumber = "CBE77412980",
            timestamp = "Nov 12, 2026 • 02:35 PM",
            verifiedBy = "Bursar Office"
        ),
        PaymentReceipt(
            receiptNumber = "RCP-2026-8829",
            studentId = "EH-1004",
            studentName = "Selamawit Tesfaye",
            grade = "KG-2",
            parentName = "W/ro Marta Tesfaye",
            tuitionPortionEtb = 11500,
            busPortionEtb = 3600,
            amountPaidEtb = 15100,
            method = PaymentMethod.CHAPA,
            referenceNumber = "CHP44510293",
            timestamp = "Nov 10, 2026 • 11:05 AM",
            verifiedBy = "Auto-Verified (Chapa)"
        ),
        PaymentReceipt(
            receiptNumber = "RCP-2026-8812",
            studentId = "EH-1006",
            studentName = "Mekdes Girma",
            grade = "Grade 4",
            parentName = "Ato Girma Desta",
            tuitionPortionEtb = 13800,
            busPortionEtb = 4000,
            amountPaidEtb = 17800,
            method = PaymentMethod.BANK_TRANSFER,
            referenceNumber = "FT263149008",
            timestamp = "Nov 05, 2026 • 04:20 PM",
            verifiedBy = "Bursar Reconciliation"
        )
    )

    fun initialNotifications(): List<SafetyNotification> = listOf(
        SafetyNotification(
            id = "NOTIF-1",
            studentId = "EH-1001",
            studentName = "Dawit Bekele",
            parentId = "PAR-01",
            busCode = "Route A",
            titleEn = "Boarded School Bus (Route A)",
            titleAm = "ወደ ት/ቤት አውቶቡስ ተሳፍሯል (መስመር A)",
            detailEn = "Checked in by W/ro Tigist Mamo at Bole Medhanialem.",
            detailAm = "በቦሌ መድኃኔዓለም በወ/ሮ ትግስት ማሞ ተረጋግጧል።",
            timestamp = "07:02 AM",
            type = BoardingStatus.BOARDED
        ),
        SafetyNotification(
            id = "NOTIF-2",
            studentId = "EH-1002",
            studentName = "Hanna Bekele",
            parentId = "PAR-01",
            busCode = "Route A",
            titleEn = "Boarded School Bus (Route A)",
            titleAm = "ወደ ት/ቤት አውቶቡስ ተሳፍሯል (መስመር A)",
            detailEn = "Checked in by W/ro Tigist Mamo at Bole Medhanialem.",
            detailAm = "በቦሌ መድኃኔዓለም በወ/ሮ ትግስት ማሞ ተረጋግጧል።",
            timestamp = "07:02 AM",
            type = BoardingStatus.BOARDED
        ),
        SafetyNotification(
            id = "NOTIF-3",
            studentId = "EH-1004",
            studentName = "Selamawit Tesfaye",
            parentId = "PAR-02",
            busCode = "Route B",
            titleEn = "Boarded School Bus (Route B)",
            titleAm = "ወደ ት/ቤት አውቶቡስ ተሳፍሯል (መስመር B)",
            detailEn = "Checked in by W/ro Genet Kassahun at Kazanchis Total.",
            detailAm = "በካዛንቺስ ቶታል በወ/ሮ ገነት ካሳሁን ተረጋግጧል።",
            timestamp = "06:56 AM",
            type = BoardingStatus.BOARDED
        ),
        SafetyNotification(
            id = "NOTIF-4",
            studentId = "EH-1006",
            studentName = "Mekdes Girma",
            parentId = "PAR-03",
            busCode = "Route C",
            titleEn = "Arrived Safely at School",
            titleAm = "ት/ቤት በሰላም ደርሳለች",
            detailEn = "Dropped off at Ethio-Horizon Campus gate.",
            detailAm = "በኢትዮ-ሆራይዘን አካዳሚ ግቢ በር ላይ በሰላም ወርዳለች።",
            timestamp = "07:42 AM",
            type = BoardingStatus.DROPPED_OFF
        )
    )

    fun initialPickupPasses(): List<PickupPass> = listOf(
        PickupPass(
            id = "PASS-101",
            code = "SR-4821",
            studentId = "EH-1002",
            studentName = "Hanna Bekele",
            parentId = "PAR-01",
            delegateName = "W/ro Kidist Tadesse",
            relationship = "Aunt / አክስት",
            delegatePhone = "+251 911 702 331",
            validDate = "Today • Afternoon Drop-off",
            isVerified = false
        )
    )

    val monthlyCollectionStats: List<MonthlyCollectionStat> = listOf(
        MonthlyCollectionStat("Sep", "መስከረም", 245000, 260000),
        MonthlyCollectionStat("Oct", "ጥቅምት", 272000, 275000),
        MonthlyCollectionStat("Nov", "ኅዳር", 238000, 280000),
        MonthlyCollectionStat("Dec", "ታኅሣሥ", 289000, 285000),
        MonthlyCollectionStat("Jan", "ጥር", 264000, 290000),
        MonthlyCollectionStat("Feb", "የካቲት", 192400, 295000)
    )
}
