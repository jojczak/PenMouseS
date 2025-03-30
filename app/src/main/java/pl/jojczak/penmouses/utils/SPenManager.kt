package pl.jojczak.penmouses.utils

import android.util.Log
import com.samsung.android.sdk.penremote.AirMotionEvent
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEvent
import com.samsung.android.sdk.penremote.SpenEventListener
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenRemote.Error.CONNECTION_FAILED
import com.samsung.android.sdk.penremote.SpenRemote.Error.UNSUPPORTED_DEVICE
import com.samsung.android.sdk.penremote.SpenUnit
import com.samsung.android.sdk.penremote.SpenUnitManager
import pl.jojczak.penmouses.di.ActivityProvider

class SPenManager(
    private val activityProvider: ActivityProvider
) {
    private var sPenUnitManager: SpenUnitManager? = null

    var onSPenMoveListener: (x: Float, y: Float) -> Unit = { _, _ -> }
    var onSPenButtonClickListener: (buttonDown: Boolean) -> Unit = {}

    fun connectToSPen() {
        Log.i(TAG, "Connecting to S-Pen...")
        val sPenRemote = SpenRemote.getInstance()

        if (sPenRemote.isFeatureEnabled(SpenRemote.FEATURE_TYPE_BUTTON) &&
            sPenRemote.isFeatureEnabled(SpenRemote.FEATURE_TYPE_AIR_MOTION)
        ) {
            if (!sPenRemote.isConnected) {
                sPenRemote.connect(activityProvider.getActivity(), ConnectionResultCallback())
            }
        }
    }

    private inner class ConnectionResultCallback : SpenRemote.ConnectionResultCallback {
        override fun onSuccess(sPenUnitManager: SpenUnitManager?) {
            Log.i(TAG, "Connection successful")
            this@SPenManager.sPenUnitManager = sPenUnitManager
        }

        override fun onFailure(errorCode: Int) {
            Log.e(TAG, "Connection failed: ${mapConnectionError(errorCode)}")
        }
    }

    fun registerButtonEventListener() {
        sPenUnitManager?.let {
            val buttonUnit = it.getUnit(SpenUnit.TYPE_BUTTON)
            it.registerSpenEventListener(ButtonEventListener(), buttonUnit)

            Log.i(TAG, "Button event listener registered")
        }
    }

    private inner class ButtonEventListener : SpenEventListener {
        override fun onEvent(event: SpenEvent) {
            val mEvent = ButtonEvent(event)

            when (mEvent.action) {
                ButtonEvent.ACTION_DOWN -> {
                    Log.d(TAG, "Button pressed")
                    onSPenButtonClickListener(true)
                }

                ButtonEvent.ACTION_UP -> {
                    Log.d(TAG, "Button released")
                    onSPenButtonClickListener(false)
                }
            }
        }
    }

    fun registerAirMotionEventListener() {
        sPenUnitManager?.let {
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.registerSpenEventListener(AirMotionEventListener(), airMotionUnit)

            Log.i(TAG, "Air motion event listener registered")
        }
    }

    private inner class AirMotionEventListener : SpenEventListener {
        override fun onEvent(event: SpenEvent) {
            val mEvent = AirMotionEvent(event)
            onSPenMoveListener(mEvent.deltaX, mEvent.deltaY)
        }
    }

    fun disconnectFromSPen() {
        Log.d(TAG, "Disconnecting from S-Pen...")
        sPenUnitManager?.let {
            val buttonUnit = it.getUnit(SpenUnit.TYPE_BUTTON)
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.unregisterSpenEventListener(buttonUnit)
            it.unregisterSpenEventListener(airMotionUnit)
        }
        SpenRemote.getInstance().disconnect(activityProvider.getActivity())
    }

    private fun mapConnectionError(errorCode: Int) = when (errorCode) {
        UNSUPPORTED_DEVICE -> "Unsupported device"
        CONNECTION_FAILED -> "Connection failed"
        else -> "Unknown error"
    }

    companion object {
        private const val TAG = "SPenManager"
    }
}