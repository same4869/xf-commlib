package com.hfzq.xfn.framework.commlib.starter.task

import android.app.Application
import android.content.Context
import android.os.Process
import com.hfzq.xfn.framework.commlib.starter.dispatcher.TaskDispatcher
import com.hfzq.xfn.framework.commlib.starter.utils.DispatcherExecutor
import java.util.concurrent.ExecutorService

/**
 * @Description:
 * @Author:         xwang
 * @CreateDate:     2020/5/18
 */

abstract class Task : ITask {
    protected var mContext: Context = TaskDispatcher.getContext()
    protected var mApplication: Application = TaskDispatcher.getApplication()

    /**
     * Task执行在哪个线程池，默认在IO的线程池；
     * CPU 密集型的一定要切换到DispatcherExecutor.getCPUExecutor();
     *
     * @return
     */
    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.getCPUExecutor()
    }

    /**
     * Task的优先级，运行在主线程则不要去改优先级
     *
     * @return
     */
    override fun priority(): Int {
        return Process.THREAD_PRIORITY_BACKGROUND
    }
}