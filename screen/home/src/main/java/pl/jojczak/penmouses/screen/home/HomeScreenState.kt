package pl.jojczak.penmouses.screen.home

import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.PenMode

data class HomeScreenState(
    val isAccessibilityEnabled: Boolean = false,

    val showStep1Dialog: Boolean = false,
    val showStep2Dialog: Boolean = false,
    val showStep3Dialog: Boolean = false,
    val showUnsupportedSPenDialog: Boolean = false,
    val showTroubleshootingDialog: Boolean = false,
    val showFirstRunDialog: Boolean = false,

    val showNotificationPermission: Boolean = false,
    val isFirstMouseLaunch: Boolean = true,

    val serviceStatus: PenMode = PenMode.Off
)