package pl.jojczak.penmouses.mousemode.basecursor

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Bitmap
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
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
import android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
import android.widget.ImageView
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.common.utils.getDisplaySize
import pl.jojczak.penmouses.mousemode.base.PenConst
import pl.jojczak.penmouses.mousemode.base.BaseMode

abstract class CursorMode(
    dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    sPenManager: SPenManager,
    context: Context,
    protected val preferences: PreferencesManager,
) : BaseMode(
    dispatchGesture = dispatchGesture,
    notificationsManager = notificationsManager,
    sPenManager = sPenManager,
    context = context,
) {
    protected val cursorState = CursorState(
        view = null,
        isSleeping = false
    )

    protected var windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    protected val displayManager =
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    protected val cursorPreferences = CursorPreferences(
        context = context,
        preferences = preferences,
    )

    protected open val cursorAnimator = CursorAnimator(
        cursorState = cursorState,
    )

    override fun start() {
        super.start()
        cursorPreferences.getBitmap()?.let { bitmap ->
            Log.d(tagName, "Cursor image loaded")

            val layoutParams = getDefaultLayoutParams()
            val (width, height) = cursorPreferences.getSize(bitmap)

            cursorState.view = createCursorView(context, bitmap)

            val (screenWidth, screenHeight) = getDisplaySize(getDisplay())

            layoutParams.x = (screenWidth - width) / 2
            layoutParams.y = (screenHeight - height) / 2
            layoutParams.width = width
            layoutParams.height = height

            Log.d(
                tagName,
                "Adding cursor view to window manager. " +
                        "Cursor spec: x: ${layoutParams.x}, y: ${layoutParams.y}, " +
                        "width: $width, height: $height"
            )

            mainHandler.post {
                windowManager.addView(cursorState.view, layoutParams)
                Log.d(tagName, "Cursor view added to window manager")
            }

            notificationsManager.showIdleNotification(context)
        } ?: run {
            Log.e(tagName, "Error getting cursor image")
        }
    }

    override fun stop() {
        super.stop()
        mainHandler.post {
            cursorState.view?.animate()?.setListener(null)
            cursorState.view?.animate()?.cancel()
            cursorState.view?.setImageDrawable(null)
            if (cursorState.view?.isAttachedToWindow == true) {
                windowManager.removeView(cursorState.view)
            }
        }
    }

    override fun updateSize() {
        Log.d(tagName, "Updating cursor size")

        cursorState.view?.let { cursor ->
            (cursor.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                val (newWidth, newHeight) = cursorPreferences.getSize(bitmap)

                updateCursorLayoutParams {
                    width = newWidth
                    height = newHeight
                }
            }
        }
    }

    override fun updateBitmap() {
        Log.d(tagName, "Updating cursor image")

        cursorState.view?.apply {
            cursorPreferences.getBitmap()?.let { bitmap ->
                post {
                    setImageBitmap(bitmap)
                    updateSize()
                }
            }
        }
    }

    private fun createCursorView(
        context: Context,
        bitmap: Bitmap
    ) = ImageView(context).apply {
        setImageBitmap(bitmap)
        setPreDrawObserver()
    }

    private fun getDefaultLayoutParams() = WindowManager.LayoutParams(
        WRAP_CONTENT,
        WRAP_CONTENT,
        TYPE_ACCESSIBILITY_OVERLAY,
        PenConst.OVERLAY_FLAGS,
        PixelFormat.TRANSLUCENT
    ).apply {
        layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        gravity = Gravity.TOP or Gravity.START
    }

    protected fun updateCursorLayoutParams(block: WindowManager.LayoutParams.() -> Unit) {
        cursorState.view?.let {
            val lp = it.layoutParams as WindowManager.LayoutParams
            lp.block()
            it.post {
                windowManager.updateViewLayout(it, lp)
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
        val lp = cursorState.view?.layoutParams as WindowManager.LayoutParams
        return Point(lp.x, lp.y)
    }

    protected fun getDisplay(): Display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
}