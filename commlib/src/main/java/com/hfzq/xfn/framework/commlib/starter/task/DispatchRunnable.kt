package com.hfzq.xfn.framework.commlib.starter.task

import android.os.Process
import android.util.Log
import com.hfzq.framework.commlib.BuildConfig
import com.hfzq.xfn.framework.commlib.starter.dispatcher.TaskDispatcher

/**
 * @Description:
 * @Author:         xwang
 * @CreateDate:     2020/5/18
 */

class DispatchRunnable : Runnable {
    private var mTask: Task
    private var mTaskDispatcher: TaskDispatcher? = null
    private val isDebug = BuildConfig.DEBUG

    constructor(task: Task) {
        mTask = task
    }

    constructor(task: Task, dispatcher: TaskDispatcher) {
        mTask = task
        mTaskDispatcher = dispatcher
    }

    override fun run() {
        val startTime = System.currentTimeMillis()
        if (isDebug) {
            Log.d(
                "starter",
                "starter-> ${mTask.javaClass.simpleName} begin run --> Thread.currentThread().id:${Thread.currentThread().id}"
            )
        }

        Process.setThreadPriority(mTask.priority())

        mTask.run()

        if (isDebug) {
            Log.d(
                "starter",
                "starter-> ${mTask.javaClass.simpleName} end  time:${(System.currentTimeMillis() - startTime)}ms"
            )
        }
    }

}