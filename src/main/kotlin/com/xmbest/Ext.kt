package com.xmbest

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import org.jetbrains.skiko.hostOs
import java.io.File

/**
 * first 当前代码resource目录
 * second 写入的文件名称
 */
val adb = Pair("adb", if (hostOs.isWindows) "adb.exe" else "adb")
val cfg = Pair("config", "config.json")
val exec = Pair("sh", if (hostOs.isWindows) "exec.bat" else "exec.sh")

/**
 * 文件分隔符
 */
const val FILE_SPLIT = "/"

/**
 * 当前程序存储目录
 */
val appStorageAbsolutePath: String =
    File(System.getProperty("user.home"), ".easyAdb").absolutePath

/**
 * 当前程序携带adb环境
 */
val programAdbAbsolutePath: String =
    File(appStorageAbsolutePath, adb.second).absolutePath

// 创建LocalSnackbarHostState，类似于LocalContext.current的使用方式
val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}