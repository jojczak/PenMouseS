package pl.jojczak.penmouses.core.common.spen

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object AppToServiceEvent {
    val serviceStatus = MutableStateFlow<PenMode>(PenMode.Off)
    val event = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    sealed class Event {
        data class Start(val mode: PenMode) : Event()
        data object Stop : Event()
        data object PreferencesUpdated: Event()
    }

    enum class PenMode {
        Off,
        Mouse,
        Point,
        Scroll
    }
}