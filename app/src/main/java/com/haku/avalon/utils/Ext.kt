package com.haku.avalon.utils

import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.haku.avalon.TAG

/**
 * 方法切换至主线程 并 延迟执行

 * @param delayMillis The delay in milliseconds before the code is executed.
 * @param func The code to be executed on the main thread.
 */
fun runOnMainThreadDelayed(delayMillis: Long, func: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        func()
    }, delayMillis)
}

fun sleep(time: Long = 1000L) {
    Thread.sleep(time)
}

fun AccessibilityNodeInfo?.tryToClick(func: (Float, Float) -> Unit): Boolean {
    if (this == null) return false
    // 获取在屏幕中的矩形位置
    val rect = Rect()
    this.getBoundsInScreen(rect)

    // 向下滑动，浏览更深内容
//                AdbUtils.runCommand("input swipe 500 1500 500 500 300")

    // 2️⃣ 计算中心点
    val clickX = rect.centerX().toFloat()
    val clickY = rect.centerY().toFloat()

    if (clickX > 0 && clickY > 0) {
        // 精确计算坐标，手动触发点击事件
        Log.d(TAG, "尝试点击位置 $clickX $clickY")
        func(clickX, clickY)
    }
    return true
}