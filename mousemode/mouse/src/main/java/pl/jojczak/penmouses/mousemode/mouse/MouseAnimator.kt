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
    var targetOpacity = 1f

    fun fadeInCursor() = view.post {
        if (view.isVisible && view.alpha == targetOpacity) return@post

        view.animate().cancel()
        view.animate()
            .alpha(targetOpacity)
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
            .withEndAction {
                view.isGone = true
            }
            .start()
    }

    companion object {
        private const val FADE_DURATION_MS = 250L
    }
}