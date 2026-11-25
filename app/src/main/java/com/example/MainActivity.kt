package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.i18n.StringsProvider
import com.example.model.AppRole
import com.example.security.PortalSecurityManager
import com.example.ui.components.OfficialReceiptDialog
import com.example.ui.components.PortalLockGate
import com.example.ui.components.TopBrandAndRoleHeader
import com.example.ui.screens.AdminPortalScreen
import com.example.ui.screens.BusAttendantScreen
import com.example.ui.screens.ParentPortalScreen
import com.example.ui.theme.SafeRideEduPayTheme
import com.example.viewmodel.SafeRideViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeRideEduPayTheme {
                SafeRideApp()
            }
        }
    }
}

@Composable
fun SafeRideApp(viewModel: SafeRideViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = remember(uiState.language) { StringsProvider.get(uiState.language) }
    val snackbarHostState = remember { SnackbarHostState() }
    val securityManager = remember(context) { PortalSecurityManager(context) }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBrandAndRoleHeader(
                strings = strings,
                activeRole = uiState.activeRole,
                language = uiState.language,
                isDriverLocked = !uiState.isDriverUnlocked,
                isParentLocked = !uiState.isParentUnlocked,
                onSelectRole = viewModel::switchRole,
                onToggleLanguage = viewModel::toggleLanguage,
                onResetDemo = viewModel::resetDemoData
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Crossfade(targetState = uiState.activeRole, label = "role_crossfade") { role ->
                    when (role) {
                        AppRole.ADMIN -> AdminPortalScreen(
                            uiState = uiState,
                            strings = strings,
                            onSearchChange = viewModel::updateSearchQuery,
                            onStatusFilter = viewModel::setStatusFilter,
                            onGradeFilter = viewModel::setGradeFilter,
                            onRouteFilter = viewModel::setRouteFilter,
                            onRecordManualReceipt = { studentId, amount, method, ref ->
                                viewModel.recordPayment(
                                    studentId = studentId,
                                    amountEtb = amount,
                                    method = method,
                                    referenceNumber = ref,
                                    verifiedBy = "Bursar Manual Verification"
                                )
                            },
                            onDispatchBulkReminders = viewModel::dispatchBulkReminders,
                            onOpenReceipt = viewModel::showReceipt
                        )

                        AppRole.BUS_ATTENDANT -> {
                            if (!uiState.isDriverUnlocked) {
                                val route = uiState.selectedRoute
                                PortalLockGate(
                                    role = AppRole.BUS_ATTENDANT,
                                    accountLabel = "${route.attendantName} (${route.code})",
                                    strings = strings,
                                    language = uiState.language,
                                    securityManager = securityManager,
                                    onUnlocked = { method ->
                                        viewModel.unlockPortal(AppRole.BUS_ATTENDANT, method)
                                    }
                                )
                            } else {
                                BusAttendantScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onSelectRoute = viewModel::selectBusRoute,
                                    onToggleTripMode = viewModel::toggleTripMode,
                                    onToggleLiveGps = viewModel::toggleLiveGpsBroadcast,
                                    onAdvanceStop = viewModel::advanceRouteStop,
                                    onMarkBoarding = viewModel::markStudentBoarding,
                                    onVerifyPickupPass = viewModel::verifyPickupPassByCode,
                                    onLockPortal = { viewModel.lockPortal(AppRole.BUS_ATTENDANT) }
                                )
                            }
                        }

                        AppRole.PARENT -> {
                            if (!uiState.isParentUnlocked) {
                                val parent = uiState.selectedParent
                                PortalLockGate(
                                    role = AppRole.PARENT,
                                    accountLabel = "${parent.fullName} (${parent.phone})",
                                    strings = strings,
                                    language = uiState.language,
                                    securityManager = securityManager,
                                    onUnlocked = { method ->
                                        viewModel.unlockPortal(AppRole.PARENT, method)
                                    }
                                )
                            } else {
                                ParentPortalScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onSelectParent = viewModel::selectParentProfile,
                                    onPayMobileMoney = { parentId, method, phone ->
                                        viewModel.payParentTotalBalance(parentId, method, phone)
                                    },
                                    onOpenReceipt = viewModel::showReceipt,
                                    onGeneratePickupPass = { studentId, name, relation, phone ->
                                        viewModel.generatePickupPass(studentId, name, relation, phone)
                                    },
                                    onShowMessage = viewModel::showMessage,
                                    onLockPortal = { viewModel.lockPortal(AppRole.PARENT) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val activeReceipt = uiState.latestGeneratedReceipt
    if (activeReceipt != null) {
        OfficialReceiptDialog(
            receipt = activeReceipt,
            language = uiState.language,
            onDismiss = { viewModel.showReceipt(null) }
        )
    }
}
