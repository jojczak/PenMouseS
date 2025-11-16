package pl.jojczak.penmouses.mousemode.basecursor

import android.graphics.Color
import android.widget.ImageView

abstract class CursorAnimator(protected val view: ImageView) {
    fun clickCursor() = view.post {
        view.setColorFilter(Color.valueOf(0f, 0f, 0f, 0.4f).toArgb())
        view.scaleY = CURSOR_DOWN_Y_SCALE
    }

    fun releaseCursor() = view.post {
        view.colorFilter = null
        view.scaleY = 1f
    }

    companion object {
        private const val CURSOR_DOWN_Y_SCALE = 0.85f
    }
}