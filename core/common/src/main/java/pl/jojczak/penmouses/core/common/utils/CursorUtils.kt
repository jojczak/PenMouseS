package pl.jojczak.penmouses.core.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Display
import android.view.Surface
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import java.io.File

private const val TAG = "CursorUtils"
const val CURSOR_IMAGE_WIDTH = 267

fun getCursorBitmap(
    context: Context,
    cursorType: CursorType,
    penMode: AppToServiceEvent.PenMode
): Bitmap? {
    return try {
        if (cursorType == CursorType.Custom) {
            val cursorFile = File(context.filesDir, CursorType.Custom.getFileName(penMode))
            BitmapFactory.decodeFile(cursorFile.absolutePath)
        } else {
            val inputStream = context.assets.open(cursorType.getFileName())
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting cursor bitmap: $cursorType", e)
        null
    }
}

fun getDisplaySize(display: Display?): Pair<Int, Int> {
    Log.d(TAG, "Getting display size. Rotation: ${display?.rotation}")
    return display?.let {
        val screenWidth =
            if (it.rotation == Surface.ROTATION_0 || it.rotation == Surface.ROTATION_180) {
                it.mode.physicalWidth
            } else {
                it.mode.physicalHeight
            }
        val screenHeight =
            if (it.rotation == Surface.ROTATION_0 || it.rotation == Surface.ROTATION_180) {
                it.mode.physicalHeight
            } else {
                it.mode.physicalWidth
            }
        screenWidth to screenHeight
    } ?: (0 to 0)
}