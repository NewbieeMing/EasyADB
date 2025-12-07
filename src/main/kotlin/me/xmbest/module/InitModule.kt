package me.xmbest.module

import io.github.vinceglb.filekit.FileKit
import me.xmbest.adb
import me.xmbest.appStorageAbsolutePath
import me.xmbest.cfg
import me.xmbest.ddmlib.DeviceManager
import me.xmbest.exec
import me.xmbest.model.Environment
import me.xmbest.util.PreferencesUtil
import me.xmbest.util.PreferencesUtil.PREFERENCES_ADB_PATH
import java.io.File

object InitModule {
    private val fileList = buildList {
        addAll(listOf(adb, cfg, exec))
    }
    private val path = appStorageAbsolutePath

    fun init() {
        writeFile()
        initAdb()
        FileKit.init("EasyADB")
    }

    private fun writeFile() {
        val parentFile = File(path)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        // 复制所需文件
        fileList.forEach {
            val fileName = it.second
            val file = File(parentFile, fileName)
            if (!file.exists()) {
                file.createNewFile()
                file.setExecutable(true)
                this::class.java.classLoader.getResourceAsStream("${it.first}/$fileName")?.use { input ->
                    input.copyTo(file.outputStream())
                }
            }
        }
    }

    private fun initAdb() {
        val savedAdbPath = PreferencesUtil.get(PREFERENCES_ADB_PATH, Environment.Program.path)
        DeviceManager.initialize(savedAdbPath)
    }
}