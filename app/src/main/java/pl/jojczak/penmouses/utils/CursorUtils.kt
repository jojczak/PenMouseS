package pl.jojczak.penmouses.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Display
import android.view.Surface
import java.io.File

private const val TAG = "CursorUtils"
const val CURSOR_IMAGE_WIDTH = 267

fun getCursorBitmap(context: Context, cursorType: CursorType): Bitmap? {
    return try {
        if (cursorType == CursorType.CUSTOM) {
            val cursorFile = File(context.filesDir, cursorType.fileName)
            BitmapFactory.decodeFile(cursorFile.absolutePath)
        } else {
            val inputStream = context.assets.open(cursorType.fileName)
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting cursor bitmap: $cursorType", e)
        null
    }
}

fun getDisplaySize(
    display: Display?,
    callback: (width: Int, height: Int) -> Unit
) {
    display?.let {
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
        callback(screenWidth, screenHeight)
    }
}