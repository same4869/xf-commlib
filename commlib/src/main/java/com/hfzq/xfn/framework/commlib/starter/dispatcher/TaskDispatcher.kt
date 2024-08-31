package com.hfzq.xfn.framework.commlib.starter.dispatcher

import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.annotation.UiThread
import com.hfzq.xfn.framework.commlib.APPLICATION
import com.hfzq.xfn.framework.commlib.isMainProcess
import com.hfzq.xfn.framework.commlib.isProtocolAgreed
import com.hfzq.xfn.framework.commlib.starter.task.DispatchRunnable
import com.hfzq.xfn.framework.commlib.starter.task.Task
import java.util.concurrent.Future

/**
 * @Description:    启动器，异步初始化调度器，对外封装类
 * @Author:         xwang
 * @CreateDate:     2020/5/18
 */

class TaskDispatcher private constructor() {
    private val mAllTasks: MutableList<Task> = mutableListOf()
    private val mFutures: MutableList<Future<*>?> = mutableListOf()

    companion object {

        @Volatile
        private var sHasInit = false
        private var sIsMainProcess = false

        fun init() {
            sHasInit = true
            sIsMainProcess = isMainProcess(APPLICATION)
        }

        fun createInstance(): TaskDispatcher {
            if (!sHasInit) {
                throw RuntimeException("must call TaskDispatcher.init first")
            }
            return TaskDispatcher()
        }

        fun getContext(): Context {
            return APPLICATION
        }

        fun getApplication(): Application {
            return APPLICATION
        }
    }

    fun addTask(task: Task): TaskDispatcher {
        mAllTasks.add(task)
        return this
    }

    fun addDelayTask(task: Task): TaskDispatcher {
        DelayInitDispatcher.addTask(task)
        return this
    }

    /**
     * 如果没有进行用户隐私同意，那么就addDelayTask，否则还是addTask
     */
    fun addAfterAgreeTask(task: Task): TaskDispatcher {
        if (isProtocolAgreed) {
            mAllTasks.add(task)
        } else {
            DelayInitDispatcher.addTask(task)
        }
        return this
    }

    @UiThread
    fun start() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw java.lang.RuntimeException("must be called from UiThread")
        }
        if (mAllTasks.size > 0) {
            sendAndExecuteAsyncTasks()
        }
    }

    /**
     * 发送去并且执行异步任务
     */
    private fun sendAndExecuteAsyncTasks() {
        for (task in mAllTasks) {
            sendTaskReal(task)
        }
    }

    /**
     * 发送任务
     */
    private fun sendTaskReal(task: Task) {
        // 直接发，是否执行取决于具体线程池
        val future: Future<*>? = task.runOn()?.submit(DispatchRunnable(task, this))
        mFutures.add(future)
    }


    fun cancel() {
        for (future in mFutures) {
            future?.cancel(true)
        }
    }
}