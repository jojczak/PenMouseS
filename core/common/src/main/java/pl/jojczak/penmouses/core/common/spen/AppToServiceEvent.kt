package pl.jojczak.penmouses.core.common.spen

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object AppToServiceEvent {
    val serviceStatus = MutableStateFlow<ModeStatus>(ModeStatus.Off)
    val event = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    sealed class Event {
        data class Start(val mode: ModeStatus) : Event()
        data object Stop : Event()
        data object PreferencesUpdated: Event()
    }

    enum class ModeStatus {
        Off,
        Loading,
        Mouse,
        Point,
        Scroll
    }
}