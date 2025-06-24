package com.haku.avalon.utils

import android.util.Log

object AdbUtils {

    private const val TAG = "HaKu_AdbUtils"

    /**
     * 执行普通 shell 命令（无 root）
     */
    fun runCommand(cmd: String): String {
        return try {
            Log.d(TAG, "执行 shell 命令: $cmd")
            val process = Runtime.getRuntime().exec(cmd)
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            process.waitFor()
            Log.d(TAG, "命令输出: $output")
            output
        } catch (e: Exception) {
            Log.e(TAG, "执行 shell 命令异常: ${e.message}")
            ""
        }
    }
}