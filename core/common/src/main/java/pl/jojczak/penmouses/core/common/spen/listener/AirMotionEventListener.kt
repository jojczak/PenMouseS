package pl.jojczak.penmouses.core.common.spen.listener

fun interface AirMotionEventListener {
    fun onEvent(deltaX: Float, deltaY: Float, timeStamp: Long)
}