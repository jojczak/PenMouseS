package pl.jojczak.penmouses.mousemode.mouse

import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import pl.jojczak.penmouses.mousemode.basecursor.CursorAnimator

class MouseAnimator(
    view: ImageView,
) : CursorAnimator(
    view = view
) {
    fun fadeInCursor() = view.post {
        if (view.isVisible && view.alpha == 1f) return@post

        view.animate().cancel()
        view.animate()
            .alpha(1f)
            .setDuration(FADE_DURATION_MS)
            .withStartAction {
                view.isVisible = true
            }
            .start()
    }

    fun fadeOutCursor() = view.post {
        if (view.isGone && view.alpha == 0f) return@post

        view.animate().cancel()
        view.animate()
            .alpha(0f)
            .setDuration(FADE_DURATION_MS)
            .withStartAction {
                view.isGone = true
            }
            .start()
    }

    companion object {
        private const val FADE_DURATION_MS = 250L
    }
}