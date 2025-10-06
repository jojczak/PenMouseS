package pl.jojczak.penmouses.service.penmodes.base.cursor

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
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
import android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import pl.jojczak.penmouses.notifications.NotificationsManager
import pl.jojczak.penmouses.service.SPenManager
import pl.jojczak.penmouses.service.penmodes.base.PenMode
import pl.jojczak.penmouses.utils.PreferencesManager
import pl.jojczak.penmouses.utils.getDisplaySize

abstract class CursorMode(
    dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    sPenManager: SPenManager,
    context: Context,
    protected val preferences: PreferencesManager,
) : PenMode(
    dispatchGesture = dispatchGesture,
    sPenManager = sPenManager,
    context = context,
) {
    protected val cursorState = CursorState(
        view = null,
        position = Point(),
        isSleeping = false
    )

    protected var windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    protected val displayManager =
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    protected val cursorPreferences = CursorPreferences(
        context = context,
        preferences = preferences,
    )

    override fun start() {
        super.start()
        cursorPreferences.getBitmap()?.let { bitmap ->
            Log.d(tagName, "Cursor image loaded")

            val layoutParams = getDefaultLayoutParams()
            val (width, height) = cursorPreferences.getSize(bitmap)

            cursorState.view = createCursorView(context, bitmap)

            val (screenWidth, screenHeight) = getDisplaySize(getDisplay())
            cursorState.position.x = (screenWidth - width) / 2
            cursorState.position.y = (screenHeight - height) / 2

            layoutParams.x = cursorState.position.x
            layoutParams.y = cursorState.position.y
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

            NotificationsManager.showIdleNotification(context)
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

                cursor.post {
                    cursor.updateLayoutParams {
                        width = newWidth
                        height = newHeight
                    }
                    windowManager.updateViewLayout(cursor, cursor.layoutParams)
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
        OVERLAY_FLAGS,
        PixelFormat.TRANSLUCENT
    ).apply {
        layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        gravity = Gravity.TOP or Gravity.START
    }

    private fun ImageView.setPreDrawObserver() {
        viewTreeObserver.addOnPreDrawListener {
            if (width <= 0 || height <= 0 || (drawable as? BitmapDrawable)?.bitmap?.isRecycled == true) {
                Log.e(tagName, "Error with cursor view in preDraw")
                return@addOnPreDrawListener false
            } else return@addOnPreDrawListener true
        }
    }

    protected fun getDisplay(): Display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)

    companion object {
        private const val OVERLAY_FLAGS = FLAG_NOT_FOCUSABLE or
                FLAG_LAYOUT_IN_SCREEN or
                FLAG_NOT_TOUCHABLE or
                FLAG_LAYOUT_NO_LIMITS
    }
}