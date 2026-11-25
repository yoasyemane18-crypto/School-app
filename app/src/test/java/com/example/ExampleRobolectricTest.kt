package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.BoardingStatus
import com.example.model.PaymentMethod
import com.example.model.PaymentStatus
import com.example.viewmodel.SafeRideViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name and initial demo dataset load properly`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        assertEquals("SafeRide & EduPay", app.getString(R.string.app_name))

        val vm = SafeRideViewModel(app)
        vm.resetDemoData()
        val state = vm.uiState.value

        assertEquals(16, state.totalEnrolled)
        assertEquals(3, state.routes.size)
        assertEquals(AppRole.ADMIN, state.activeRole)
        assertEquals(AppLanguage.EN, state.language)
    }

    @Test
    fun `mobile money checkout updates ledger balance and generates receipt`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = SafeRideViewModel(app)
        vm.resetDemoData()

        // Dawit Bekele (EH-1001, PAR-01) starts with 8,300 ETB balance
        val initialDawit = vm.uiState.value.students.first { it.id == "EH-1001" }
        assertTrue(initialDawit.balanceDueEtb > 0)

        val receipt = vm.payParentTotalBalance("PAR-01", PaymentMethod.TELEBIRR, "+251911234567")
        assertNotNull(receipt)

        val updatedDawit = vm.uiState.value.students.first { it.id == "EH-1001" }
        assertEquals(0, updatedDawit.balanceDueEtb)
        assertEquals(PaymentStatus.PAID, updatedDawit.paymentStatus)

        // Verify persistence across new ViewModel instance
        val reloadedVm = SafeRideViewModel(app)
        val persistedDawit = reloadedVm.uiState.value.students.first { it.id == "EH-1001" }
        assertEquals(PaymentStatus.PAID, persistedDawit.paymentStatus)

        // Reset restores initial balance
        reloadedVm.resetDemoData()
        val resetDawit = reloadedVm.uiState.value.students.first { it.id == "EH-1001" }
        assertTrue(resetDawit.balanceDueEtb > 0)
    }

    @Test
    fun `bus boarding tap triggers parent safety notification and qr handoff verifies`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = SafeRideViewModel(app)
        vm.resetDemoData()

        val initialNotifCount = vm.uiState.value.notifications.size
        vm.markStudentBoarding("EH-1008", BoardingStatus.BOARDED)

        val afterNotifState = vm.uiState.value
        assertEquals(initialNotifCount + 1, afterNotifState.notifications.size)
        assertEquals("EH-1008", afterNotifState.notifications.first().studentId)
        assertEquals(BoardingStatus.BOARDED, afterNotifState.students.first { it.id == "EH-1008" }.boardingStatus)

        val pass = vm.generatePickupPass("EH-1001", "Ato Dawit Uncle", "Uncle", "+251911999888")
        assertNotNull(pass)
        val verified = vm.verifyPickupPassByCode(pass!!.code)
        assertNotNull(verified)
        assertTrue(verified!!.isVerified)
    }
}
