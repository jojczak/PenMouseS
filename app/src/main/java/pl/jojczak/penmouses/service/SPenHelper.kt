package pl.jojczak.penmouses.service

import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.WindowManager
import pl.jojczak.penmouses.utils.SPenManager

class SPenHelper(
    sPenManager: SPenManager,
    private val params: WindowManager.LayoutParams,
    private val display: Display,
    private val performTouch: (sPenPath: Path) -> Unit,
    private val updateLayout: () -> Unit
) {
    private var isSPenPressed = false
    private var pressedTime = 0L
    private var sPenPath = Path()

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

        params.x = params.x.coerceIn(0, display.mode.physicalWidth)
        params.y = params.y.coerceIn(0, display.mode.physicalHeight)

        updateLayout()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            sPenPath.lineTo(params.x.toFloat(), params.y.toFloat())
            pressedTime += TICK_TIME

            if (isSPenPressed && pressedTime < MAX_BUTTON_DOWN_TIME) {
                handler.postDelayed(this, TICK_TIME)
            } else {
                performTouch(sPenPath)
            }
        }
    }

    private fun onSPenButtonClick(buttonDown: Boolean) {
        if (buttonDown) {
            isSPenPressed = true
            pressedTime = 0
            sPenPath.reset()
            sPenPath.moveTo(params.x.toFloat(), params.y.toFloat())
            handler.post(runnable)
        } else {
            isSPenPressed = false
        }
    }

    companion object {
        private const val DELAY_TO_EVENT_REGISTER = 2000L
        private const val CURSOR_SENSITIVITY = 700f

        private const val MAX_BUTTON_DOWN_TIME = 1000
        private const val TICK_TIME = 50L
    }
}