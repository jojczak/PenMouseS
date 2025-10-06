package pl.jojczak.penmouses.service.penmodes.mouse

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.core.view.isGone
import androidx.core.view.isVisible
import pl.jojczak.penmouses.service.penmodes.base.PenRunnable
import pl.jojczak.penmouses.service.penmodes.base.cursor.CursorState

class MouseAnimator(
    private val cursorState: CursorState,
    private val mainHandler: Handler,
    private val windowManager: WindowManager,
    onCursorHidden: () -> Unit,
    onCursorShown: () -> Unit,
    onCursorSleep: () -> Unit,
    onCursorWakeup: () -> Unit,
    getCanStartJobs: () -> Boolean
) {
    fun clickCursor() = cursorState.view?.post {
        cursorState.view?.setColorFilter(Color.valueOf(0f, 0f, 0f, 0.4f).toArgb())
        cursorState.view?.scaleY = CURSOR_DOWN_Y_SCALE
    }

    fun releaseCursor() = cursorState.view?.post {
        cursorState.view?.colorFilter = null
        cursorState.view?.scaleY = 1f
    }

    fun showCursor(hideDelay: Long) {
        Log.d(TAG, "Starting show cursor animation")

        hideCursor(hideDelay)
        putCursorToSleep(hideDelay + SLEEP_DELAY_MS)

        if (cursorState.view?.isVisible == true && !cursorState.isSleeping) {
            Log.d(TAG, "Cursor already visible")
            return
        }

        mainHandler.post(showCursorJob)
    }

    private val showCursorJob = PenRunnable.create(getCanStartJobs()) {
        Log.d(TAG, "Showing cursor")

        if (cursorState.isSleeping) {
            cursorState.isSleeping = false
            onCursorWakeup()
        }

        cursorState.view?.let { cursorView ->
            cursorView.animate()
                .alpha(1f)
                .setDuration(FADE_DURATION_MS)
                .withStartAction {
                    cursorView.isVisible = true
                    onCursorShown()
                }
                .start()
        } ?: run {
            Log.e(TAG, "Error getting cursor view")
        }
    }

    fun hideCursor(hideDelay: Long) {
        if (cursorState.view?.isGone == true) {
            Log.d(TAG, "Cursor already hidden")
            return
        }

        mainHandler.removeCallbacks(hideCursorJob)
        mainHandler.postDelayed(hideCursorJob, hideDelay)
    }

    private val hideCursorJob = PenRunnable.create(getCanStartJobs()) {
        Log.d(TAG, "Hiding cursor")

        cursorState.view?.let { cursorView ->
            if (cursorView.isGone) {
                Log.d(TAG, "Cursor already hidden")
                return@create
            }

            cursorView.animate()
                .alpha(0f)
                .setDuration(FADE_DURATION_MS)
                .withEndAction {
                    cursorView.isGone = true
                    onCursorHidden()
                }
                .start()
        } ?: run {
            Log.e(TAG, "Error getting cursor view")
        }
    }

    private fun putCursorToSleep(sleepDelay: Long) {
        if (cursorState.isSleeping) {
            Log.d(TAG, "Cursor already sleeping")
            return
        }

        mainHandler.removeCallbacks(sleepCursorJob)
        mainHandler.postDelayed(sleepCursorJob, sleepDelay)
    }

    private val sleepCursorJob = PenRunnable.create(getCanStartJobs()) {
        Log.d(TAG, "Sleeping cursor")

        cursorState.isSleeping = true
        onCursorSleep()
    }

    val updateCursorViewPosition = object : PenRunnable(getCanStartJobs()) {
        override fun callback() {
            cursorState.view?.let { cursorView ->
                if (!cursorView.isAttachedToWindow) return

                val lp = cursorView.layoutParams as WindowManager.LayoutParams

                val startX = lp.x
                val startY = lp.y
                val targetX = cursorState.position.x
                val targetY = cursorState.position.y

                if (startX == targetX && startY == targetY) {
                    mainHandler.postDelayed(this, POSITION_UPDATE_INTERVAL_MS)
                    return@let
                }

                val runnableContext = this

                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = POSITION_UPDATE_INTERVAL_MS
                    interpolator = LinearInterpolator()

                    addUpdateListener { animator ->
                        if (!cursorView.isAttachedToWindow) return@addUpdateListener

                        val fraction = animator.animatedValue as Float
                        lp.x = (startX + (targetX - startX) * fraction).toInt()
                        lp.y = (startY + (targetY - startY) * fraction).toInt()
                        windowManager.updateViewLayout(cursorView, lp)

                        if (fraction == 1f) {
                            mainHandler.post(runnableContext)
                        }
                    }
                    start()
                }
            }
        }
    }

    companion object {
        private const val TAG = "CursorAnimator"
        private const val FADE_DURATION_MS = 250L
        private const val POSITION_UPDATE_INTERVAL_MS = 35L
        private const val CURSOR_DOWN_Y_SCALE = 0.85f
        private const val SLEEP_DELAY_MS = 10000L
    }
}