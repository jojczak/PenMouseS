package pl.jojczak.penmouses.core.common.spen.listener

import android.util.Log
import pl.jojczak.penmouses.core.common.spen.SPenManager.Companion.mapConnectionError

open class ConnectionResultCallback {
    open fun onSuccess() = Unit
    open fun onFailure(errorCode: Int) {
        Log.e("SPenManager", "Connection failed: ${mapConnectionError(errorCode)}")
    }
}