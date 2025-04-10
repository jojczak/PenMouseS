package pl.jojczak.penmouses.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.os.UserManager
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
import android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.utils.SPenManager
import javax.inject.Inject

@AndroidEntryPoint
class MouseService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var appToServiceEventObserver: Job? = null

    private var windowManager: WindowManager? = null
    private var cursorView: ImageView? = null
    private var params: WindowManager.LayoutParams? = null
    private var display: Display? = null

    @Inject
    lateinit var sPenManager: SPenManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        val userManager = getSystemService(Context.USER_SERVICE) as UserManager
        userManager.isUserAGoat
        registerReceiver()
    }

    private fun registerReceiver() {
        cancelAppToServiceEventObserver()
        appToServiceEventObserver = serviceScope.launch {
            AppToServiceEvent.event.collect { event ->
                when (event) {
                    AppToServiceEvent.Event.Start -> startAirMouse()
                    AppToServiceEvent.Event.Stop -> stopAirMouse(true)
                    AppToServiceEvent.Event.StopOnDestroy -> stopAirMouse(false)
                }
            }
        }
    }

    private fun startAirMouse() {
        Handler(Looper.getMainLooper()).post {
            setupWindowManagerAndDisplay()
            setupOverlayParams()
            setupCursorView()
            setupSPen()
        }
    }

    private fun stopAirMouse(withSPenManager: Boolean) {
        Handler(Looper.getMainLooper()).post {
            windowManager?.removeView(cursorView)
            if (withSPenManager) sPenManager.disconnectFromSPen()
            windowManager = null
            cursorView = null
            params = null
            display = null
        }
    }

    // Step 1: Set up WindowManager and Display
    private fun setupWindowManagerAndDisplay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    }

    // Step 2: Set up WindowManager.LayoutParams
    private fun setupOverlayParams() {
        val overlayFlags = FLAG_NOT_FOCUSABLE or
                FLAG_LAYOUT_IN_SCREEN or
                FLAG_NOT_TOUCHABLE or
                FLAG_LAYOUT_NO_LIMITS

        params = WindowManager.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT,
            TYPE_ACCESSIBILITY_OVERLAY,
            overlayFlags,
            PixelFormat.TRANSLUCENT
        ).apply {
            layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            gravity = Gravity.TOP or Gravity.START
        }
    }

    // Step 3: Set up ImageView for the cursor
    private fun setupCursorView() {
        val drawable = getDrawable(R.drawable.cursor) ?: return
        val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()

        cursorView = ImageView(this).apply {
            setImageResource(R.drawable.cursor)
        }

        params?.width = (CURSOR_SIZE * ratio).toInt()
        params?.height = CURSOR_SIZE

        windowManager?.addView(cursorView, params)
    }

    // Step 4: Set up S-Pen event listeners
    private fun setupSPen() {
        sPenManager.connectToSPen(
            performTouch = ::performTouch,
            updateLayout = ::updateLayout
        )
    }

    private fun updateLayout(pos: Point) {
        Handler(Looper.getMainLooper()).post {
            params?.x = pos.x
            params?.y = pos.y
            windowManager?.updateViewLayout(cursorView, params)
        }
    }

    private fun performTouch(sPenPath: Path) {
        Log.d(TAG, "performTouch")

        val gestureStroke = GestureDescription.StrokeDescription(sPenPath, 0, TOUCH_DURATION)
        val gestureBuilder = GestureDescription.Builder().apply {
            addStroke(gestureStroke)
        }
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    private fun cancelAppToServiceEventObserver() {
        appToServiceEventObserver?.cancel()
        appToServiceEventObserver = null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopAirMouse(true)
        cancelAppToServiceEventObserver()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit

    companion object {
        private const val TAG = "MouseService"

        private const val CURSOR_SIZE = 200
        private const val TOUCH_DURATION = 100L
    }
}