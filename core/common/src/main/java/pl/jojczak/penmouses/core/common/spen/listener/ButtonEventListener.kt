package pl.jojczak.penmouses.core.common.spen.listener

import androidx.annotation.IntDef
import com.samsung.android.sdk.penremote.ButtonEvent

@IntDef(ButtonEvent.ACTION_UP, ButtonEvent.ACTION_DOWN)
@Retention(AnnotationRetention.SOURCE)
annotation class ButtonAction

fun interface ButtonEventListener {
    fun onEvent(@ButtonAction type: Int, timeStamp: Long)
}