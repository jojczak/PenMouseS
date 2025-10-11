package pl.jojczak.penmouses.mousemode.basecursor

import android.widget.ImageView

data class CursorState(
    var view: ImageView?,
    var isSleeping: Boolean
)