package pl.jojczak.penmouses.ui.home

import pl.jojczak.penmouses.service.AppToServiceEvent

data class HomeScreenState(
    val isAccessibilityEnabled: Boolean = false,

    val showStep1Dialog: Boolean = false,
    val showStep2Dialog: Boolean = false,
    val showStep3Dialog: Boolean = false,
    val showUnsupportedSPenDialog: Boolean = false,
    val showTroubleshootingDialog: Boolean = false,

    val showNotificationPermission: Boolean = false,

    val serviceStatus: AppToServiceEvent.ServiceStatus = AppToServiceEvent.ServiceStatus.OFF
)