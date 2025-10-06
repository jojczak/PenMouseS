package pl.jojczak.penmouses.service.listeners

import android.util.Log
import pl.jojczak.penmouses.service.SPenManager.Companion.mapConnectionError

open class ConnectionResultCallback {
    open fun onSuccess() = Unit
    open fun onFailure(errorCode: Int) {
        Log.e("SPenManager", "Connection failed: ${mapConnectionError(errorCode)}")
    }
}