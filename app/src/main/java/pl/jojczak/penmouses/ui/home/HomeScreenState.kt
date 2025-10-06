package pl.jojczak.penmouses.ui.home

import pl.jojczak.penmouses.service.penmodes.base.PenMode
import kotlin.reflect.KClass

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

    val serviceStatus: KClass<out PenMode>? = null
)