package com.hfzq.xfn.framework.commlib.starter.task

import android.os.Process
import androidx.annotation.IntRange
import java.util.concurrent.Executor

interface ITask {
    fun run()

    /**
     * Task执行所在的线程池，可指定，一般默认
     *
     * @return
     */
    fun runOn(): Executor?

    /**
     * 优先级的范围，可根据Task重要程度及工作量指定；之后根据实际情况决定是否有必要放更大
     *
     * @return
     */
    @IntRange(
        from = Process.THREAD_PRIORITY_FOREGROUND.toLong(),
        to = Process.THREAD_PRIORITY_LOWEST.toLong()
    )
    fun priority(): Int
}