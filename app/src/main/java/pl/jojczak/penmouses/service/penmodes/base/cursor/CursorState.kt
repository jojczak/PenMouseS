package pl.jojczak.penmouses.service.penmodes.base.cursor

import android.graphics.Point
import android.widget.ImageView

data class CursorState(
    var view: ImageView?,
    val position: Point,
    var isSleeping: Boolean
)