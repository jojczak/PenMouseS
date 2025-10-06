package pl.jojczak.penmouses.service.penmodes

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.os.Handler
import pl.jojczak.penmouses.service.SPenManager
import pl.jojczak.penmouses.service.penmodes.base.PenMode

class PointMode(
    dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    sPenManager: SPenManager,
    context: Context
) : PenMode(
    dispatchGesture = dispatchGesture,
    sPenManager = sPenManager,
    context = context
) {
}