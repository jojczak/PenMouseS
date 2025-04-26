package pl.jojczak.penmouses.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

private const val TAG = "CursorUtils"
const val CURSOR_IMAGE_HEIGHT = 400

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