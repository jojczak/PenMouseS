package pl.jojczak.penmouses.service

import kotlinx.coroutines.flow.MutableSharedFlow

object AppToServiceEvent {
    val event = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
}