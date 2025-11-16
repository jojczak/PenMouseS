package pl.jojczak.penmouses.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.ui.R as coreR

data class HomeScreenState(
    val isAccessibilityEnabled: Boolean = false,
    val unsupportedDeviceDialogEnabled: Boolean = false,
    val firstRunDialogEnabled: Boolean = false,
    val serviceStatus: ModeStatus = ModeStatus.Off
)

sealed class HomeScreenEvent {
    data object TryToShowReviewDialog : HomeScreenEvent()
}

sealed class HomeViewAction {
    data class LifecycleEvent(val state: Lifecycle.State) : HomeViewAction()
    data class ToggleUnsupportedDeviceDialog(val enabled: Boolean) : HomeViewAction()
    data class ToggleFirstRunDialog(val enabled: Boolean) : HomeViewAction()
    data class SendEventToService(val event: AppToServiceEvent.Event) : HomeViewAction()
}

data class ModesComponentData(
    val mode: ModeStatus,
    @param:StringRes val labelId: Int,
    @param:DrawableRes val iconId: Int,
    @param:DrawableRes val iconActiveId: Int,
)

internal val modesComponentData = listOf(
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