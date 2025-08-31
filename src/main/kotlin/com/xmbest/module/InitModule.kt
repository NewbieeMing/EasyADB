package com.xmbest.module

import androidx.compose.ui.res.useResource
import com.xmbest.adb
import com.xmbest.appStorageAbsolutePath
import com.xmbest.cfg
import com.xmbest.ddmlib.DeviceManager
import com.xmbest.exec
import com.xmbest.model.Environment
import io.github.vinceglb.filekit.FileKit
import java.io.File

object InitModule {
    private val fileList = buildList {
        addAll(listOf(adb, cfg, exec))
    }
    private val path = appStorageAbsolutePath

    fun init() {
        writeFile()
        loadConfig()
        DeviceManager.initialize(Environment.System.path)
        FileKit.init("EasyADB")
    }

    fun writeFile() {
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
                useResource(it.first + "/" + fileName) { input ->
                    input.copyTo(file.outputStream())
                }
            }
        }
    }


    fun loadConfig() {

    }
}