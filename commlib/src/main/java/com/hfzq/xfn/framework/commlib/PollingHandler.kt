package com.hfzq.xfn.framework.commlib

import android.os.Handler
import android.os.Looper

class PollingHandler {
    private val INTERVAL_TIME = 3000L
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    fun startPolling(action: () -> Unit) {
        runnable = Runnable {
            action()
            scheduleNextPoll()
        }
        runnable?.let { handler.post(it) }
    }

    private fun scheduleNextPoll() {
        runnable?.let { handler.postDelayed(it, INTERVAL_TIME) }
    }

    fun stopPolling() {
        handler.removeCallbacksAndMessages(null)
    }
}