package pl.jojczak.penmouses.core.common.spen

import android.util.Log
import com.samsung.android.sdk.penremote.AirMotionEvent
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEventListener
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenUnit
import com.samsung.android.sdk.penremote.SpenUnitManager
import pl.jojczak.penmouses.core.common.di.ActivityProvider
import pl.jojczak.penmouses.core.common.spen.listener.AirMotionEventListener
import pl.jojczak.penmouses.core.common.spen.listener.ButtonEventListener
import pl.jojczak.penmouses.core.common.spen.listener.ConnectionResultCallback

class SPenManager(
    private val activityProvider: ActivityProvider,
) {
    var isSPenButtonDown = false

    private var sPenUnitManager: SpenUnitManager? = null
    private var isAirMotionRegistered = false

    fun connect(sPenManagerConnectionResultCallback: ConnectionResultCallback) {
        Log.i(TAG, "Connecting to S-Pen...")

        val activity = activityProvider.getActivity() ?: run {
            Log.e(TAG, "Activity is null, cannot connect to S-Pen")
            sPenManagerConnectionResultCallback.onFailure(SpenRemote.Error.UNKNOWN)
            return
        }

        if (isSPenSupported()) {
            val sPenRemote = SpenRemote.getInstance()

            if (!sPenRemote.isConnected) {
                sPenRemote.connect(
                    activity,
                    SPenRemoteConnectionResultCallback(sPenManagerConnectionResultCallback)
                )
            }
        } else {
            Log.w(TAG, "S-Pen is not supported, cannot connect")
            sPenManagerConnectionResultCallback.onFailure(SpenRemote.Error.UNSUPPORTED_DEVICE)
        }
    }

    private inner class SPenRemoteConnectionResultCallback(
        val sPenManagerConnectionResultCallback: ConnectionResultCallback
    ) : SpenRemote.ConnectionResultCallback {
        override fun onSuccess(sPenUnitManager: SpenUnitManager?) {
            Log.i(TAG, "Connection successful")
            this@SPenManager.sPenUnitManager = sPenUnitManager
            sPenManagerConnectionResultCallback.onSuccess()
        }

        override fun onFailure(errorCode: Int) {
            sPenManagerConnectionResultCallback.onFailure(errorCode)
        }
    }

    fun registerButtonEventListener(buttonEventListener: ButtonEventListener) {
        sPenUnitManager?.let { unitManager ->
            val buttonUnit = unitManager.getUnit(SpenUnit.TYPE_BUTTON)

            val mButtonEventListener = SpenEventListener { event ->
                val mEvent = ButtonEvent(event)

                if (!isSPenButtonDown && mEvent.action == ButtonEvent.ACTION_UP) return@SpenEventListener
                isSPenButtonDown = mEvent.action == ButtonEvent.ACTION_DOWN

                buttonEventListener.onEvent(mEvent.action, mEvent.timeStamp)
            }

            unitManager.registerSpenEventListener(mButtonEventListener, buttonUnit)

            Log.i(TAG, "Button event listener registered")
        }
    }

    fun registerAirMotionEventListener(airMotionEventListener: AirMotionEventListener) {
        if (isAirMotionRegistered) {
            Log.w(TAG, "Air motion event listener already registered")
            return
        }
        isAirMotionRegistered = true

        sPenUnitManager?.let { unitManager ->
            val airMotionUnit = unitManager.getUnit(SpenUnit.TYPE_AIR_MOTION)

            val mAirMotionEventListener = SpenEventListener { event ->
                val mEvent = AirMotionEvent(event)
                airMotionEventListener.onEvent(mEvent.deltaX, mEvent.deltaY, mEvent.timeStamp)
            }

            unitManager.registerSpenEventListener(mAirMotionEventListener, airMotionUnit)

            Log.i(TAG, "Air motion event listener registered")
        }
    }

    fun unregisterAirMotionEventListener() {
        isAirMotionRegistered = false

        Log.d(TAG, "Unregistering air motion event listener")
        sPenUnitManager?.let {
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.unregisterSpenEventListener(airMotionUnit)
        }
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting from S-Pen...")

        sPenUnitManager?.let {
            val buttonUnit = it.getUnit(SpenUnit.TYPE_BUTTON)
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.unregisterSpenEventListener(buttonUnit)
            it.unregisterSpenEventListener(airMotionUnit)
            isAirMotionRegistered = false
        }
        sPenUnitManager = null

        try {
            SpenRemote.getInstance().disconnect(activityProvider.getActivity())
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error disconnecting from S-Pen, probably activity destroyed before disconnect",
                e
            )
        }
    }

    companion object {
        private const val TAG = "SPenManager"

        fun mapConnectionError(errorCode: Int) = when (errorCode) {
            SpenRemote.Error.UNSUPPORTED_DEVICE -> "Unsupported device"
            SpenRemote.Error.CONNECTION_FAILED -> "Connection failed"
            else -> "Unknown error"
        }

        fun isSPenSupported() = try {
            val sPenClass = Class.forName("com.samsung.android.sdk.penremote.SpenRemote")
            val sPenRemote = sPenClass.getMethod("getInstance").invoke(null)

            val isButtonEnabled = sPenClass.getMethod("isFeatureEnabled", Int::class.java)
                .invoke(sPenRemote, SpenRemote.FEATURE_TYPE_BUTTON) as Boolean
            val isAirMotionEnabled = sPenClass.getMethod("isFeatureEnabled", Int::class.java)
                .invoke(sPenRemote, SpenRemote.FEATURE_TYPE_AIR_MOTION) as Boolean

            if (isButtonEnabled && isAirMotionEnabled) {
                Log.i(TAG, "S-Pen is supported")
                true
            } else {
                Log.w(TAG, "S-Pen is not supported")
                false
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "SpenRemote class not found, S-Pen features unavailable", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if S-Pen is supported", e)
            false
        }
    }
}