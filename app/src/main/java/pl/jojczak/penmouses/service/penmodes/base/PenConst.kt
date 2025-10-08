package pl.jojczak.penmouses.service.penmodes.base

import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

object PenConst {
    const val DELAY_TO_EVENT_REGISTER_MS = 1000L
    const val MAX_CLICK_TIME_MS = 1000L
    const val CLICK_PEN_UPDATE_INTERVAL_MS = 25L
    const val OVERLAY_FLAGS = FLAG_NOT_FOCUSABLE or
            FLAG_LAYOUT_IN_SCREEN or
            FLAG_NOT_TOUCHABLE or
            FLAG_LAYOUT_NO_LIMITS
}