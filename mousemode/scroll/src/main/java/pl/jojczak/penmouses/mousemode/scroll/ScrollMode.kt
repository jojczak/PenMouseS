package pl.jojczak.penmouses.mousemode.scroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Context
import android.graphics.Path
import android.hardware.display.DisplayManager
import android.os.Handler
import android.util.Log
import android.view.Display
import com.samsung.android.sdk.penremote.ButtonEvent
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.spen.listener.ButtonAction
import pl.jojczak.penmouses.core.common.spen.listener.ConnectionResultCallback
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.common.utils.getDisplaySize
import pl.jojczak.penmouses.mousemode.base.BaseMode
import pl.jojczak.penmouses.mousemode.base.PenConst

class ScrollMode(
    dispatchGesture: (GestureDescription, AccessibilityService.GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    preferences: PreferencesManager,
    sPenManager: SPenManager,
    stopService: () -> Unit,
    context: Context,
) : BaseMode(
    notificationsManager = notificationsManager,
    dispatchGesture = dispatchGesture,
    preferences = preferences,
    sPenManager = sPenManager,
    stopService = stopService,
    context = context,
) {
    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private var isExperimentalModeEnabled: Boolean = false
    private var deltaY = 0f

    override fun start() {
        super.start()
        preferencesUpdated()

        sPenManager.connect(object : ConnectionResultCallback() {
            override fun onSuccess() {
                mainHandler.postDelayed({
                    if (!isExperimentalModeEnabled) {
                        sPenManager.registerAirMotionEventListener(
                            ::onAirMotionEvent,
                            ::showErrorAndStopService
                        )
                    }
                    sPenManager.registerButtonEventListener(
                        ::onButtonEvent,
                        ::showErrorAndStopService
                    )
                }, PenConst.DELAY_TO_EVENT_REGISTER_MS)
            }
        })
    }

    private fun onButtonEvent(@ButtonAction type: Int, timeStamp: Long) {
        Log.d(tagName, "Button action: $type")

        if (type == ButtonEvent.ACTION_DOWN) {
            deltaY = 0f
            if (isExperimentalModeEnabled) {
                sPenManager.registerAirMotionEventListener(
                    ::onAirMotionEvent,
                    ::showErrorAndStopService
                )
            }
        } else if (type == ButtonEvent.ACTION_UP) {
            if (isExperimentalModeEnabled) {
                sPenManager.unregisterAirMotionEventListener()
            }
            performScrollGesture()

            Log.d(tagName, "DeltaY: $deltaY")
        }
    }

    private fun onAirMotionEvent(deltaX: Float, deltaY: Float, timeStamp: Long) {
        this@ScrollMode.deltaY += deltaY
    }

    private fun performScrollGesture() {
        val isScrollingUp = deltaY > 0

        val (screenWidth, screenHeight) = getDisplaySize(displayManager.getDisplay(Display.DEFAULT_DISPLAY))
        val centerX = screenWidth / 2f

        val topY = screenHeight * ((VERTICAL_SCROLL_SCREEN_FRACTION - 1f) / VERTICAL_SCROLL_SCREEN_FRACTION)
        val bottomY = screenHeight * (1f / VERTICAL_SCROLL_SCREEN_FRACTION)

        val startY = if (isScrollingUp) topY else bottomY
        val endY = if (isScrollingUp) bottomY else topY

        val scrollPath = Path().apply {
            moveTo(centerX, startY)
            lineTo(centerX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(StrokeDescription(scrollPath, 0L, GESTURE_TIME_MS))
            .build()

        dispatchGesture(gesture, null, null)
    }

    override fun preferencesUpdated() {
        super.preferencesUpdated()
        isExperimentalModeEnabled = preferences.get(PrefKeys.SCROLL_EXPERIMENTAL_MODE)
    }

    companion object {
        private const val VERTICAL_SCROLL_SCREEN_FRACTION = 5f
        private const val GESTURE_TIME_MS = 150L
    }
}