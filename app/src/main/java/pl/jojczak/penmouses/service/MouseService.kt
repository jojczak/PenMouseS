package pl.jojczak.penmouses.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Bitmap
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.utils.PrefKeys
import pl.jojczak.penmouses.utils.PreferencesManager
import pl.jojczak.penmouses.utils.SPenManager
import pl.jojczak.penmouses.utils.getCursorBitmap
import javax.inject.Inject

@AndroidEntryPoint
class MouseService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var appToServiceEventObserver: Job? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private var windowManager: WindowManager? = null
    private var cursorView: ImageView? = null
    private var display: Display? = null
    private var params: WindowManager.LayoutParams? = null
    private var cursorImageBitmap: Bitmap? = null
    private var isAirMouseRunning = false

    @Inject
    lateinit var preferences: PreferencesManager

    @Inject
    lateinit var sPenManager: SPenManager

    @Inject
    @ApplicationContext
    lateinit var context: Context

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
                    AppToServiceEvent.Event.UpdateSensitivity -> sPenManager.updateSPenSensitivity()
                    AppToServiceEvent.Event.UpdateCursorSize -> mainHandler.post { setupParams() }
                    AppToServiceEvent.Event.UpdateCursorType -> mainHandler.post { setupCursorImage() }
                }
            }
        }
    }

    // Step 1: Check if AirMouse is already running, if not, start it
    private fun startAirMouse() {
        if (isAirMouseRunning) {
            Log.w(TAG, "AirMouse is already running")
            return
        }
        isAirMouseRunning = true

        Log.d(TAG, "Starting AirMouse")
        mainHandler.post {
            setupWindowManagerAndDisplay()
            setupOverlayParams()
            setupCursorImage()
            setupParams()
            setupCursorView()
            setupSPen()
        }
    }

    // Stopping air mouse, clearing all variables
    private fun stopAirMouse(withSPenManager: Boolean) {
        Log.d(TAG, "Stopping AirMouse")
        mainHandler.post {
            windowManager?.removeView(cursorView)
            if (withSPenManager) sPenManager.disconnectFromSPen()
            windowManager = null
            cursorView = null
            params = null
            display = null
            cursorImageBitmap?.recycle()
            cursorImageBitmap = null
            isAirMouseRunning = false
        }
    }

    // Step 2: Set up WindowManager and Display
    private fun setupWindowManagerAndDisplay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    }

    // Step 3: Set up WindowManager.LayoutParams
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

    // Step 4: Setting up cursor image | Updating cursor image when running
    private fun setupCursorImage() {
        val cursorType = preferences.get(PrefKeys.CURSOR_TYPE)
        cursorImageBitmap = getCursorBitmap(context, cursorType)
        cursorView?.setImageBitmap(cursorImageBitmap)
    }

    // Step 5: Setting up cursor size | Updating cursor size when running
    private fun setupParams() {
        val prefSize = preferences.get(PrefKeys.CURSOR_SIZE)
        val density = context.resources.displayMetrics.density
        val cursorSize = (prefSize * density).toInt()

        cursorImageBitmap?.let { imageBitmap ->
            val ratio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()

            params?.width = (cursorSize * ratio).toInt()
            params?.height = cursorSize

            cursorView?.let {
                windowManager?.updateViewLayout(cursorView, params)
            }
        }
    }

    // Step 6: Set up cursor view
    private fun setupCursorView() {
        cursorImageBitmap?.let { imageBitmap ->
            cursorView = ImageView(this).apply {
                setImageBitmap(imageBitmap)
            }
            windowManager?.addView(cursorView, params)
        }
    }

    // Step 7: Set up S-Pen event listeners
    private fun setupSPen() {
        sPenManager.connectToSPen(
            performTouch = ::performTouch,
            updateLayout = ::updateLayout
        )
    }

    // Layout update when S-Pen moves
    private fun updateLayout(pos: Point) {
        mainHandler.post {
            params?.x = pos.x
            params?.y = pos.y
            windowManager?.updateViewLayout(cursorView, params)
        }
    }

    // Simulate touch event via AccessibilityService when S-Pen button is clicked
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

        private const val TOUCH_DURATION = 100L
    }
}