package com.hfzq.xfn.framework.commlib

import android.os.Handler
import android.os.Looper

object MainThreadHandler {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    fun runOnMainThread(runnable: Runnable) {
        //如果当前是主线程，则直接执行
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            runnable.run()
        } else {
            mainThreadHandler.post(runnable)
        }
    }

    fun post(runnable: Runnable) {
        mainThreadHandler.post(runnable)
    }
}