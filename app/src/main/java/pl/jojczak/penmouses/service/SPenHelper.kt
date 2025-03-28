package pl.jojczak.penmouses.service

import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.WindowManager
import pl.jojczak.penmouses.utils.SPenManager

class SPenHelper(
    sPenManager: SPenManager,
    private val params: WindowManager.LayoutParams,
    private val display: Display,
    private val performClick: (down: ButtonEvent, up: ButtonEvent) -> Unit,
    private val updateLayout: () -> Unit
) {

    init {
        sPenManager.apply {
            onSPenMoveListener = ::onSPenMoved
            onSPenButtonClickListener = ::onSPenButtonClick
            connectToSPen()

            Handler(Looper.getMainLooper()).postDelayed({
                registerButtonEventListener()
                registerAirMotionEventListener()
            }, DELAY_TO_EVENT_REGISTER)
        }
    }

    private fun onSPenMoved(x: Float, y: Float) {
        params.x += (x * CURSOR_SENSITIVITY).toInt()
        params.y -= (y * CURSOR_SENSITIVITY).toInt()

        if (params.x < 0) params.x = 0
        if (params.x > display.mode.physicalWidth) params.x = display.mode.physicalWidth
        if (params.y < 0) params.y = 0
        if (params.y > display.mode.physicalHeight) params.y = display.mode.physicalHeight

        updateLayout()
    }

    private var buttonDownEvent = ButtonEvent(0f, 0f)

    private fun onSPenButtonClick(buttonDown: Boolean) {
        if (buttonDown) {
            buttonDownEvent = ButtonEvent(params.x.toFloat(), params.y.toFloat())
        } else {
            val buttonUpEvent = ButtonEvent(params.x.toFloat(), params.y.toFloat())
            performClick(buttonDownEvent, buttonUpEvent)
        }
    }

    data class ButtonEvent(
        val x: Float,
        val y: Float,
        val timestamp: Long = System.currentTimeMillis()
    )

    companion object {
        private const val DELAY_TO_EVENT_REGISTER = 2000L
        private const val CURSOR_SENSITIVITY = 700f
    }
}