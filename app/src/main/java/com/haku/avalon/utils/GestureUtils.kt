package com.haku.avalon.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.graphics.Path

object GestureUtils {

    private const val TAG = "GestureUtils"

    /**
     * 执行滑动手势
     */
    fun performSwipe(
        service: AccessibilityService,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long = 300,
        callback: (() -> Unit)? = null
    ) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()

        service.dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                callback?.invoke()
            }
        }, null)
    }

    /**
     * 垂直滑动
     * @param distance 滑动距离(像素)，正数=下滑，负数=上滑
     */
    fun verticalSwipe(
        service: AccessibilityService,
        startX: Float,
        startY: Float,
        distance: Float,
        duration: Long = 300
    ) {
        performSwipe(
            service = service,
            startX = startX,
            startY = startY,
            endX = startX,
            endY = startY + distance,
            duration = duration
        )
    }
}