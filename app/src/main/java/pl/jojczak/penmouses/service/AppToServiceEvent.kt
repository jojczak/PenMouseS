package pl.jojczak.penmouses.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.jojczak.penmouses.service.penmodes.base.PenMode
import kotlin.reflect.KClass

object AppToServiceEvent {
    val serviceStatus = MutableStateFlow<KClass<out PenMode>?>(null)
    val event = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    sealed class Event {
        data class Start(val mode: KClass<out PenMode>?) : Event()
        data object Stop : Event()
        data object UpdateSensitivity : Event()
        data object UpdateCursorSize : Event()
        data object UpdateCursorBitmap : Event()
        data object UpdateHideDelay : Event()
        data object UpdateSPenSleepEnabled : Event()
    }
}