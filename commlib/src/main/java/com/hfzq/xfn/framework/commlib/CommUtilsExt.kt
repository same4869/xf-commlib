package com.hfzq.xfn.framework.commlib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.hfzq.framework.commlib.R
import com.hfzq.framework.commlib.databinding.ViewToastBinding
import com.jakewharton.rxbinding3.view.clicks
import com.pokemon.mebius.commlib.utils.MebiusSPUtil
import io.reactivex.functions.Consumer
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

lateinit var APPLICATION: Application

typealias SimpleOnClickListener = () -> Unit

var isProtocolAgreed: Boolean = false

//点击事件，防抖500ms
fun View.onClick(onClick: SimpleOnClickListener) {
    throttleFirstClick(Consumer { onClick() })
}

fun View.onClick(duration: Long, onClick: SimpleOnClickListener) {
    throttleFirstClick(duration, Consumer { onClick() })
}

@SuppressLint("CheckResult")
internal fun View.throttleFirstClick(duration: Long, action: Consumer<Any?>) {
    this.clicks().throttleFirst(duration, TimeUnit.MILLISECONDS).subscribe({
        try {
            action.accept(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, {
        it.printStackTrace()
    }).disposeOnDestroyByContext(context)
}

//点击事件，防抖500ms
@SuppressLint("CheckResult")
internal fun View.throttleFirstClick(action: Consumer<Any?>) {
    this.clicks().throttleFirst(500L, TimeUnit.MILLISECONDS).subscribe({
        try {
            action.accept(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, {
        it.printStackTrace()
    }).disposeOnDestroyByContext(context)
}

fun getAppVersionName(): String {
    val manager = APPLICATION.packageManager
    var name = ""
    try {
        val info = manager.getPackageInfo(APPLICATION.packageName, 0)
        name = info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return name
}

fun getAppVersionCode(): Int {
    val manager = APPLICATION.packageManager
    var code = 0
    try {
        val info = manager.getPackageInfo(APPLICATION.packageName, 0)
        code = info.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return code
}

//运营商 isWantCode只返回码不返回运营商名字
fun getSubscriptionOperatorType(context: Context, isWantCode: Boolean = false): String {
    var opeType = "Unknown"
    // No sim
    if (!hasSim(context)) {
        return opeType
    }
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val operator = tm.simOperator
    if (isWantCode) return operator
    // 中国联通
    opeType = if ("46001" == operator || "46006" == operator || "46009" == operator) {
        "中国联通"
    } else if ("46000" == operator || "46002" == operator || "46004" == operator || "46007" == operator) {
        "中国移动"
    } else if ("46003" == operator || "46005" == operator || "46011" == operator) {
        "中国电信"
    } else {
        "Unknown"
    }
    return opeType
}

private fun hasSim(context: Context): Boolean {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val operator = tm.simOperator
    return !TextUtils.isEmpty(operator)
}

//通用toast显示方法
private var mToast: Toast? = null

fun showToast(
    @StringRes message: Int, isCancelLast: Boolean = true, isShowBackground: Boolean = false
) {
    showToast(
        APPLICATION.getString(message),
        isCancelLast = isCancelLast,
        isShowBackground = isShowBackground
    )
}

/**
 * isCancelLast:显示前是否先取消掉之前的（如果有，防止toast叠加
 * isShowBackground:是否允许应用后台是弹toast
 */
fun showToast(
    message: String?,
    duration: Int = Toast.LENGTH_SHORT,
    location: Int = Gravity.CENTER,
    isCancelLast: Boolean = true,
    isShowBackground: Boolean = false,
    yOffset: Int = 0
) {
    if (message.isNullOrEmpty()) {
        return
    }
    if (!isShowBackground && !isAppForeground(APPLICATION)) {
        return
    }
    if (isCancelLast) {
        mToast?.cancel()
    }

    try {
        val binding = ViewToastBinding.inflate(LayoutInflater.from(APPLICATION))
        if (MebiusSPUtil.getInstance().getInt("dark_skin", 0) == 0) {
            binding.llRootViewToast.setBackgroundResource(R.drawable.bg_comm_gray_first_round5)
        } else {
            binding.llRootViewToast.setBackgroundResource(R.drawable.bg_shape_e55a5a5a_conner_8dp)
        }
        binding.mToastTv.text = message
        mToast = Toast.makeText(APPLICATION, message, duration).apply {
            setGravity(location, 0, getPrefectYOffset(location, yOffset))
            view = binding.root
        }
        mToast?.show()
    } catch (e: Exception) {
        //解决在子线程中调用Toast的异常情况处理
        Looper.prepare()
        val binding = ViewToastBinding.inflate(LayoutInflater.from(APPLICATION))
        if (MebiusSPUtil.getInstance().getInt("dark_skin", 0) == 0) {
            binding.llRootViewToast.setBackgroundResource(R.drawable.bg_comm_gray_first_round5)
        } else {
            binding.llRootViewToast.setBackgroundResource(R.drawable.bg_shape_e55a5a5a_conner_8dp)
        }
        binding.mToastTv.text = message
        mToast = Toast.makeText(APPLICATION, message, duration).apply {
            setGravity(location, 0, getPrefectYOffset(location, yOffset))
            view = binding.root
        }
        mToast?.show()
        Looper.loop()
    }
}

private val defaultYOffset = 70.dp2px

private fun getPrefectYOffset(location: Int, yOffset: Int): Int {
    if (location == Gravity.BOTTOM) {
        return defaultYOffset.coerceAtLeast(yOffset)
    }
    return yOffset
}

/**
 * 应用是否处于前台
 * @return
 */
fun isAppForeground(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcessInfos = manager.runningAppProcesses
    appProcessInfos?.forEach {
        //当前应用处于运行中，并且在前台
        if (it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return true
        }
    }
    return false
}

/**
 * 软键盘是否打开
 */
fun hasSoftKeys(windowManager: WindowManager): Boolean {
    val d = windowManager.defaultDisplay
    val realDisplayMetrics = DisplayMetrics()
    d.getRealMetrics(realDisplayMetrics)
    val realHeight = realDisplayMetrics.heightPixels
    val realWidth = realDisplayMetrics.widthPixels
    val displayMetrics = DisplayMetrics()
    d.getMetrics(displayMetrics)
    val displayHeight = displayMetrics.heightPixels
    val displayWidth = displayMetrics.widthPixels
    return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
}


fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        this.windowToken,
        0
    )
}

fun String?.toSafeInt(def: Int = 0): Int {
    this ?: return def
    return try {
        this.toInt(10)
    } catch (e: NumberFormatException) {
        def
    }
}

fun String?.toSafeLong(): Long {
    this ?: return 0L
    return try {
        this.toLong()
    } catch (e: NumberFormatException) {
        0L
    }
}

fun String?.toSafeFloat(): Float {
    this ?: return 0F
    return try {
        this.toFloat()
    } catch (e: NumberFormatException) {
        0F
    }
}

fun String?.toSafeDouble(): Double {
    this ?: return 0.0
    return try {
        this.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

fun String?.toSafeBoolean(def: Boolean = false): Boolean {
    this ?: return def
    return try {
        this.toBoolean()
    } catch (e: Exception) {
        def
    }
}

fun <T> ArrayList<T>.safeAdd(position: Int, item: T) {
    when {
        position < 0 -> add(0, item)
        position > size -> add(item)
        else -> add(position, item)
    }
}

fun <T> ArrayList<T>.safeSet(position: Int, item: T) {
    if (position in indices) {
        set(position, item)
    }
}

/**
 * 兼容 Android 6.0 的 removeIf
 * 原代码 [java.util.Collection.removeIf]
 */
fun <E> MutableCollection<E>.safeRemoveIf(predicate: (E) -> Boolean): Boolean {
    var removed = false
    val each = iterator()
    while (each.hasNext()) {
        if (predicate.invoke(each.next())) {
            each.remove()
            removed = true
        }
    }
    return removed
}

/**
 * 判断当前是否为主进程
 */
fun isMainProcess(context: Context): Boolean {
    return getCurrentProcessName() == context.packageName
}

/**
 * 获取当前 context 所在进程名称
 */
@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
fun getCurrentProcessName(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName()
    } else {
        var processName = ""
        try {
            val declaredMethod: Method = Class.forName(
                "android.app.ActivityThread", false,
                Application::class.java.classLoader
            ).getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
            declaredMethod.isAccessible = true
            val invoke = declaredMethod.invoke(null, *arrayOf<Any>())
            if (invoke is String) {
                processName = invoke
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        processName
    }
}