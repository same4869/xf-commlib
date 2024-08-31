package com.hfzq.xfn.framework.commlib.starter.dispatcher

import android.os.Looper
import android.os.MessageQueue.IdleHandler
import com.hfzq.xfn.framework.commlib.starter.task.DispatchRunnable
import com.hfzq.xfn.framework.commlib.starter.task.Task
import java.util.*

/**
 * @Description:
 * @Author:         xwang
 * @CreateDate:     2020/5/18
 */

object DelayInitDispatcher {
    private val mDelayTasks: Queue<Task> = LinkedList<Task>()

    private val mIdleHandler = IdleHandler {
        if (mDelayTasks.size > 0) {
            val task = mDelayTasks.poll()
            DispatchRunnable(task).run()
        }
        !mDelayTasks.isEmpty()
    }

    fun addTask(task: Task?): DelayInitDispatcher? {
        mDelayTasks.add(task)
        return this
    }

    fun start() {
        Looper.myQueue().addIdleHandler(mIdleHandler)
    }

}