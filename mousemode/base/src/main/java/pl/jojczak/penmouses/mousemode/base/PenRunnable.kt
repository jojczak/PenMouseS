package pl.jojczak.penmouses.mousemode.base

import android.util.Log

open class PenRunnable(
    private val canStartJobs: Boolean
) : Runnable {
    override fun run() {
        if (!canStartJobs) {
            Log.d(TAG, "Can't start jobs")
            return
        }
        callback()
    }

    open fun callback() = Unit

    companion object {
        private const val TAG = "PenRunnable"

        fun create(canStartJobs: Boolean, callback: () -> Unit) =
            object : PenRunnable(canStartJobs) {
                override fun callback() = callback()
            }
    }
}