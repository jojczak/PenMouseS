package pl.jojczak.penmouses.service.listeners

fun interface AirMotionEventListener {
    fun onEvent(deltaX: Float, deltaY: Float, timeStamp: Long)
}