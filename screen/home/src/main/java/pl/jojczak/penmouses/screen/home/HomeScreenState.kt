package pl.jojczak.penmouses.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import pl.jojczak.penmouses.core.ui.R as coreR
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus

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

    val serviceStatus: ModeStatus = ModeStatus.Off
)

data class ModesComponentData(
    val mode: ModeStatus,
    @param:StringRes val labelId: Int,
    @param:DrawableRes val iconId: Int,
    @param:DrawableRes val iconActiveId: Int,
)

val modesComponentData = listOf(
    ModesComponentData(
        mode = ModeStatus.Off,
        labelId = coreR.string.pen_mode_off,
        iconId = coreR.drawable.ic_off_mode,
        iconActiveId = coreR.drawable.ic_off_mode_filled,
    ),
    ModesComponentData(
        mode = ModeStatus.Mouse,
        labelId = coreR.string.pen_mode_mouse,
        iconId = coreR.drawable.ic_mouse_mode,
        iconActiveId = coreR.drawable.ic_mouse_mode_filled,
    ),
    ModesComponentData(
        mode = ModeStatus.Point,
        labelId = coreR.string.pen_mode_point,
        iconId = coreR.drawable.ic_point_mode,
        iconActiveId = coreR.drawable.ic_point_mode,
    ),
    ModesComponentData(
        mode = ModeStatus.Scroll,
        labelId = coreR.string.pen_mode_scroll,
        iconId = coreR.drawable.ic_scroll_mode,
        iconActiveId = coreR.drawable.ic_scroll_mode_filled,
    ),
)