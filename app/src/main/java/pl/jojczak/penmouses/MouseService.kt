package pl.jojczak.penmouses

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView

class MouseService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private lateinit var cursorView: ImageView
    private lateinit var params: WindowManager.LayoutParams

    override fun onServiceConnected() {
        super.onServiceConnected()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS

        params.gravity = Gravity.BOTTOM or Gravity.START

        val drawable = getDrawable(R.drawable.cursor) ?: return
        val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()

        cursorView = ImageView(this).apply {
            setImageResource(R.drawable.cursor)
        }

        params.width = (CURSOR_SIZE * ratio).toInt()
        params.height = CURSOR_SIZE

        params.x = 500
        params.y = 500


        windowManager.addView(cursorView, params)

//        cursorView.layoutParams = WindowManager.LayoutParams().apply {
//            this.x = 200
//        }

        val sPenManager = (application as PenMouseSApp).sPenManager
        sPenManager.connectToSPen { x, y ->
            params.x += (x * CURSOR_SENSITIVITY).toInt()
            params.y += (y * CURSOR_SENSITIVITY).toInt()

            Handler(Looper.getMainLooper()).post {
                windowManager.updateViewLayout(cursorView, params)
            }

        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {}

    companion object {
        private const val CURSOR_SIZE = 200
        private const val CURSOR_SENSITIVITY = 20f
    }
}