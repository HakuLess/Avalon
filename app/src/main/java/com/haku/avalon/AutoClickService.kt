package com.haku.avalon

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.haku.avalon.utils.GestureUtils
import com.haku.avalon.utils.runOnMainThreadDelayed
import com.haku.avalon.utils.tryToClick

const val TAG = "HaKuService"

class AutoClickService : AccessibilityService() {

    // 单次事件只触发一次逻辑，防止频繁重复触发
    private var isDoing = false

    // 延迟操作时间，等待页面切换、弹窗等UI稳定
    private val DELAY = 2000L

    private val btnTextList = arrayListOf<Pair<String, Boolean>>(
        Pair("完成", true),
        Pair("我要报名", true),
        Pair("确认报名", true),
        Pair("已报名", false),
        Pair("免费抽", true),
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        // 服务启动时提示
        Log.d(TAG, "自动点击服务已启动")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (isDoing) return

        // 每次窗口或视图变化时回调
        val root = rootInActiveWindow ?: return

        // 示例：查找文本为“允许”的按钮并点击
//        Log.d(TAG, "Root 查找免费试按钮: ${allowNodes}")

        isDoing = true

        // 延迟1s后执行，防止点击事件无法命中
        runOnMainThreadDelayed(DELAY) {

            isDoing = false

            // 任意点击触发
            var anyTrigger = false

            for ((text, isFirst) in btnTextList) {
                val applyResult = root.findAccessibilityNodeInfosByText(text).let {
                    if (isFirst) it.firstOrNull()
                    else it.lastOrNull()
                }

                Log.d(TAG, "Find $text with ${applyResult != null}")
                val ans = applyResult?.tryToClick({ x, y -> clickByGesture(x, y) })
                if (ans == null) continue

                Log.d(TAG, "Try to press $text $ans")
                anyTrigger = true
                if (ans) return@runOnMainThreadDelayed
            }

            // 无任何触发，则延迟滑动屏幕一次
            if (!anyTrigger) {
                GestureUtils.verticalSwipe(service = this, startX = 500f, startY = 1000f, distance = -100f, duration = 500L)
            }
        }
    }

    override fun onInterrupt() {
        // 服务被中断时
    }

    /**
     * 通过手势在指定坐标模拟点击
     */
    fun clickByGesture(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val desc = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 1000))
            .build()
        val ans = dispatchGesture(desc, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d(TAG, "手势执行完成")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.d(TAG, "手势被取消")
            }
        }, null)

        Log.d(TAG, "dispatchGesture is $ans")
    }
}
