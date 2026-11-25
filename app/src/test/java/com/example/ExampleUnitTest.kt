package com.example

import com.example.data.DemoSeedData
import com.example.i18n.StringsProvider
import com.example.model.AppLanguage
import com.example.model.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun seedData_containsSixteenStudentsAndThreeRoutes() {
        val students = DemoSeedData.initialStudents()
        val routes = DemoSeedData.initialRoutes()

        assertEquals(16, students.size)
        assertEquals(3, routes.size)
        assertTrue(students.any { it.paymentStatus == PaymentStatus.PAID })
        assertTrue(students.any { it.paymentStatus == PaymentStatus.PARTIAL })
        assertTrue(students.any { it.paymentStatus == PaymentStatus.OVERDUE })
    }

    @Test
    fun studentBalanceAndStatus_calculateCorrectly() {
        val dawit = DemoSeedData.initialStudents().first { it.id == "EH-1001" }
        assertEquals(18300, dawit.totalFeeEtb)
        assertEquals(8300, dawit.balanceDueEtb)
        assertEquals(PaymentStatus.PARTIAL, dawit.paymentStatus)

        val paidDawit = dawit.copy(totalPaidEtb = dawit.totalFeeEtb)
        assertEquals(0, paidDawit.balanceDueEtb)
        assertEquals(PaymentStatus.PAID, paidDawit.paymentStatus)
    }

    @Test
    fun bilingualStrings_provideNonEmptyLabelsForEnglishAndAmharic() {
        val en = StringsProvider.get(AppLanguage.EN)
        val am = StringsProvider.get(AppLanguage.AM)

        assertTrue(en.sendBulkReminder.isNotBlank())
        assertTrue(am.sendBulkReminder.isNotBlank())
        assertTrue(en.unlockBiometric.isNotBlank())
        assertTrue(am.unlockBiometric.isNotBlank())
        assertTrue(en.unlockCredentialManager.isNotBlank())
        assertTrue(am.unlockCredentialManager.isNotBlank())
        assertTrue(en.etbUnit == "ETB")
        assertTrue(am.etbUnit == "ብር")
    }
}
