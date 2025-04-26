package pl.jojczak.penmouses.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object AppToServiceEvent {
    val serviceStatus = MutableStateFlow(ServiceStatus.OFF)
    val event = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    sealed class Event {
        data object Start : Event()
        data object Stop : Event()
        data object StopOnDestroy : Event()
        data object UpdateSensitivity : Event()
        data object UpdateCursorSize : Event()
        data object UpdateCursorType : Event()
    }

    enum class ServiceStatus {
        ON,
        OFF
    }
}