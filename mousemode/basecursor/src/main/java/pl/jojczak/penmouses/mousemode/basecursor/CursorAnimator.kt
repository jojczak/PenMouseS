package pl.jojczak.penmouses.mousemode.basecursor

import android.graphics.Color

open class CursorAnimator(
    private val cursorState: CursorState,
) {
    fun clickCursor() = cursorState.view?.post {
        cursorState.view?.setColorFilter(Color.valueOf(0f, 0f, 0f, 0.4f).toArgb())
        cursorState.view?.scaleY = CURSOR_DOWN_Y_SCALE
    }

    fun releaseCursor() = cursorState.view?.post {
        cursorState.view?.colorFilter = null
        cursorState.view?.scaleY = 1f
    }

    companion object {
        private const val CURSOR_DOWN_Y_SCALE = 0.85f
    }
}