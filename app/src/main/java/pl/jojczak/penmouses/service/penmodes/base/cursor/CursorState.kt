package pl.jojczak.penmouses.service.penmodes.base.cursor

import android.widget.ImageView

data class CursorState(
    var view: ImageView?,
    var isSleeping: Boolean
)