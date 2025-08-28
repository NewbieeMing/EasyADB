package com.xmbest

import org.jetbrains.skiko.hostOs
import java.io.File

/**
 * first 当前代码resource目录
 * second 写入的文件名称
 */
val adb = Pair("adb", if (hostOs.isWindows) "adb.exe" else "adb")
val cfg = Pair("config", "config.json")

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
