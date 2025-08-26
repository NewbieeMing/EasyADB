package com.xmbest.ddmlib

import com.android.ddmlib.FileListingService
import com.android.ddmlib.InstallReceiver
import com.android.ddmlib.MultiLineReceiver
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.Image
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DeviceOperate {
    private const val TAG = "DeviceOperate"

    private val device
        get() = DeviceManager.device.value

    private val fileListingService
        get() = FileManager.fileListingService.value

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName(TAG))

    fun root(): Boolean {
        Log.i(TAG, "adb root")
        return device?.root() ?: false
    }

    fun forceStop(applicationName: String) {
        Log.i(TAG, "adb shell force-stop $applicationName")
        device?.forceStop(applicationName)
    }

    /**
     * 需要设备root后，未root的机器推荐forceStop
     */
    fun kill(pids: List<Int>) {
        val pidStr = pids.joinToString(" ")
        Log.i(TAG, "adb shell kill $pidStr")
        shell("kill $pidStr")
    }

    fun rm(filePath: String) {
        Log.i(TAG, "adb shell rm -rf $filePath")
        device?.removeRemotePackage(filePath)
    }

    fun inputKey(key: Int) {
        Log.i(TAG, "adb shell input keyevent $key")
        shell("input keyevent $key")
    }

    fun reboot() {
        Log.i(TAG, "adb reboot")
        device?.reboot(null)
    }

    suspend fun path(packageName: String): String {
        return shell("pm path $packageName", 300L)
    }

    fun mkdir(path: String, auth: Int) {
        Log.i(TAG, "adb shell mkdir -m $auth $path")
        shell("mkdir -m $auth $path")
    }

    fun touch(path: String) {
        Log.i(TAG, "adb shell touch $path")
        shell("touch $path")
    }

    fun chmod(path: String, auth: Int) {
        Log.i(TAG, "adb shell chmod $auth $path")
        shell("chmod $auth $path")
    }

    fun push(local: String, remote: String) {
        device?.pushFile(local, remote)
    }

    fun pull(remote: String, local: String) {
        device?.pullFile(remote, local)
    }

    /**
     * 安装应用
     * @param remoteFilePath 安装路径
     * @return 是否安装成功
     */
    suspend fun install(remoteFilePath: String) = suspendCoroutine {
        device?.installRemotePackage(remoteFilePath, true, object : InstallReceiver() {
            override fun done() {
                it.resume(
                    if (isSuccessfullyCompleted) InstallState.Success(successMessage)
                    else InstallState.Error(errorCode, errorMessage)
                )
            }
        }, "-t") ?: it.resume(InstallState.NotConnected)
    }

    /**
     * 卸载应用
     * @param packageName 应用包名
     * @return null success else error msg
     */
    fun uninstall(packageName: String): String? {
        return device?.uninstallPackage(packageName)
    }

    /**
     * 截图
     * @param needWriteClipboard 是否写入剪切板
     */
    fun screenshot(needWriteClipboard: Boolean = true): Image? {
        val image = device?.screenshot?.asBufferedImage() ?: return null
        if (needWriteClipboard) {
            ClipboardUtil.setClipboardImage(image)
        }
        return image
    }

    /**
     * 查找当前文件列表
     */
    suspend fun ls(parentPath: String) = suspendCoroutine {
        val suspendSingle = it
        fileListingService?.apply {
            getChildren(
                FileListingService.FileEntry(
                    root,
                    parentPath,
                    FileListingService.TYPE_DIRECTORY,
                    device?.isRoot == true
                ),
                false,
                object : FileListingService.IListingReceiver {
                    override fun setChildren(
                        entry: FileListingService.FileEntry?,
                        children: Array<out FileListingService.FileEntry>?
                    ) {
                        suspendSingle.resume(children?.asList() ?: emptyList())
                    }

                    override fun refreshEntry(entry: FileListingService.FileEntry?) {
                    }
                }
            )
        }
    }

    fun shell(command: String) = device?.executeShellCommand(command, EmptyReceiver())

    suspend fun shell(command: String, timeMillis: Long) = suspendCoroutine {
        val suspendSingle = it
        coroutineScope.launch {
            device?.executeShellCommand(command, object : MultiLineReceiver() {
                override fun processNewLines(lines: Array<out String>?) {
                    if (lines?.isNotEmpty() == true && isActive) {
                        val str = lines.filter { line -> line.isNotEmpty() }.joinToString("\n")
                        suspendSingle.resume(str)
                    }
                }

                override fun isCancelled() = false
            })
            delay(timeMillis)
            suspendSingle.resume("")
        }
    }
}
    