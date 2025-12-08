package pl.jojczak.penmouses.mousemode.basecursor

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.hardware.display.DisplayManager
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
import android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
import android.widget.ImageView
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.common.utils.getDisplaySize
import pl.jojczak.penmouses.mousemode.base.BaseMode
import pl.jojczak.penmouses.mousemode.base.PenConst

abstract class CursorMode<T : CursorAnimator>(
    dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    preferences: PreferencesManager,
    sPenManager: SPenManager,
    stopService: () -> Unit,
    context: Context,
    private val modeStatus: ModeStatus,
    val animatorFactory: (ImageView) -> T
) : BaseMode(
    notificationsManager = notificationsManager,
    dispatchGesture = dispatchGesture,
    preferences = preferences,
    sPenManager = sPenManager,
    stopService = stopService,
    context = context,
) {

    protected var windowManager = getNewWindowManager()
    protected var displayManager = getNewDisplayManager()

    protected val cursorPreferences = CursorPreferences(
        context = context,
        preferences = preferences,
    )

    protected val view = ImageView(context)

    protected val cursorAnimator: T by lazy { animatorFactory(view) }

    override fun start() {
        super.start()
        cursorPreferences.getBitmap(modeStatus)?.let { bitmap ->
            Log.d(tagName, "Cursor image loaded")

            val layoutParams = getDefaultLayoutParams()
            val (width, height) = cursorPreferences.getSize(modeStatus, bitmap)

            view.setImageBitmap(bitmap)
            view.alpha = cursorPreferences.getOpacity(modeStatus)
            view.setPreDrawObserver()

            displayManager = getNewDisplayManager()
            val (screenWidth, screenHeight) = getDisplaySize(getDisplay())

            layoutParams.x = (screenWidth - width) / 2
            layoutParams.y = (screenHeight - height) / 2
            layoutParams.width = width
            layoutParams.height = height

            windowManager = getNewWindowManager()

            Log.d(
                tagName,
                "Adding cursor view to window manager. " +
                        "Cursor spec: x: ${layoutParams.x}, y: ${layoutParams.y}, " +
                        "width: $width, height: $height"
            )

            mainHandler.post {
                try {
                    windowManager.addView(view, layoutParams)
                } catch (e: Exception) {
                    Log.e(tagName, "Error adding cursor view to window manager", e)
                    Firebase.crashlytics.recordException(e)
                    showErrorAndStopService(ERROR_WINDOW_MANAGER)
                }
            }

            Log.d(tagName, "Cursor view added to window manager")

            notificationsManager.showIdleNotification(context)
        } ?: run {
            Log.e(tagName, "Error getting cursor image")
        }
    }

    override fun stop() {
        super.stop()
        mainHandler.post {
            view.animate().setListener(null)
            view.animate().cancel()
            view.setImageDrawable(null)
            if (view.isAttachedToWindow) {
                windowManager.removeView(view)
            }
        }
    }

    override fun preferencesUpdated() {
        super.preferencesUpdated()
        updateSize()
        updateBitmap()
        updateOpacity()
    }

    private fun updateSize() {
        Log.d(tagName, "Updating cursor size")

        (view.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
            val (newWidth, newHeight) = cursorPreferences.getSize(modeStatus, bitmap)

            updateCursorLayoutParams {
                width = newWidth
                height = newHeight
            }
        }
    }

    private fun updateBitmap() {
        Log.d(tagName, "Updating cursor image")

        cursorPreferences.getBitmap(modeStatus)?.let { bitmap ->
            view.post {
                view.setImageBitmap(bitmap)
                updateSize()
            }
        }
    }

    private fun updateOpacity() {
        Log.d(tagName, "Updating cursor opacity")
        view.post { view.alpha = cursorPreferences.getOpacity(modeStatus) }
    }

    private fun getDefaultLayoutParams() = WindowManager.LayoutParams(
        WRAP_CONTENT,
        WRAP_CONTENT,
        TYPE_ACCESSIBILITY_OVERLAY,
        PenConst.OVERLAY_FLAGS,
        PixelFormat.TRANSLUCENT
    ).apply {
        layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        gravity = Gravity.TOP or Gravity.START
    }

    protected fun updateCursorLayoutParams(block: WindowManager.LayoutParams.() -> Unit) {
        val lp = view.layoutParams as WindowManager.LayoutParams
        lp.block()
        view.post {
            if (view.isAttachedToWindow) {
                windowManager.updateViewLayout(view, lp)
            }
        }
    }

    private fun ImageView.setPreDrawObserver() {
        viewTreeObserver.addOnPreDrawListener {
            if (width <= 0 || height <= 0 || (drawable as? BitmapDrawable)?.bitmap?.isRecycled == true) {
                Log.e(tagName, "Error with cursor view in preDraw")
                return@addOnPreDrawListener false
            } else return@addOnPreDrawListener true
        }
    }

    protected fun getCursorPos(): Point {
        val lp = view.layoutParams as WindowManager.LayoutParams
        return Point(lp.x, lp.y)
    }

    protected fun getDisplay(): Display =
        displayManager.getDisplay(Display.DEFAULT_DISPLAY)

    private fun getNewWindowManager() =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private fun getNewDisplayManager() =
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
}