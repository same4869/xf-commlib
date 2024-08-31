package com.hfzq.xfn.framework.commlib.starter.utils

import android.util.Log
import com.hfzq.framework.commlib.BuildConfig
import java.lang.Integer.max
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @Description:
 * @Author:         xwang
 * @CreateDate:     2020/5/18
 */

object DispatcherExecutor {

    /**
     * CPU 密集型任务的线程池
     */
    private var sCPUThreadPoolExecutor: ThreadPoolExecutor? = null

    /**
     * IO 密集型任务的线程池
     */
    private var sIOThreadPoolExecutor: ExecutorService? = null

    /**
     * CPU 核数
     */
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()

    /**
     * 线程池线程数
     */
    private val CORE_POOL_SIZE = max(CPU_COUNT - 1, 5)

    /**
     * 线程池线程数的最大值
     */
    private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

    private const val KEEP_ALIVE_SECONDS = 8L

    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()

    private val sHandler =
        RejectedExecutionHandler { r, _ -> Executors.newCachedThreadPool().execute(r) }

    private val sThreadFactory: DefaultThreadFactory = DefaultThreadFactory()

    init {
        sCPUThreadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
            sPoolWorkQueue, sThreadFactory, sHandler
        )
        sCPUThreadPoolExecutor?.allowCoreThreadTimeOut(true)
        if (BuildConfig.DEBUG) {
            Log.d(
                "starter",
                "starter sPoolWorkQueue:$sPoolWorkQueue sThreadFactory:$sThreadFactory sHandler:$sHandler CORE_POOL_SIZE:$CORE_POOL_SIZE MAXIMUM_POOL_SIZE:$MAXIMUM_POOL_SIZE KEEP_ALIVE_SECONDS:$KEEP_ALIVE_SECONDS"
            )
        }
        sIOThreadPoolExecutor = Executors.newCachedThreadPool(sThreadFactory)
    }

    /**
     * 获取CPU线程池
     *
     * @return
     */
    fun getCPUExecutor(): ThreadPoolExecutor? {
        return sCPUThreadPoolExecutor
    }

    /**
     * 获取IO线程池
     *
     * @return
     */
    fun getIOExecutor(): ExecutorService? {
        return sIOThreadPoolExecutor
    }

    class DefaultThreadFactory : ThreadFactory {
        private val poolNumber = AtomicInteger(1)
        private var group: ThreadGroup? = null
        private val threadNumber = AtomicInteger(1)
        private var namePrefix: String? = null

        init {
            val s = System.getSecurityManager()
            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            namePrefix = "TaskDispatcherPool-${poolNumber.getAndIncrement()}-Thread-"
        }

        override fun newThread(r: Runnable?): Thread {
            val t = Thread(
                group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0
            )
            if (t.isDaemon) {
                t.isDaemon = false
            }
            if (t.priority != Thread.NORM_PRIORITY) {
                t.priority = Thread.NORM_PRIORITY
            }
            return t
        }
    }
}