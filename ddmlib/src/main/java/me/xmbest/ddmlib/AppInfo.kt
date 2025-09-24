package me.xmbest.ddmlib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

data class AppInfo(
    var path: String = "",
    val packageName: String,
    var versionName: String = "",
    var versionCode: String = "",
    var minSdk: String = "",
    var targetSdk: String = "",
    var size: String = "",
)

suspend fun AppInfo.loadInfo() {
    withContext(Dispatchers.IO) {
        val versionDeferred = async { DeviceOperate.getAppVersion(packageName) }
        val sizeDeferred = async { DeviceOperate.getFileSize(packageName) }
        val version = versionDeferred.await()
        size = sizeDeferred.await()
        Log.d("AppInfo", "versionInfo : $version")
        versionCode = version["versionCode"] ?: ""
        versionName = version["versionName"] ?: ""
        minSdk = version["minSdk"] ?: ""
        targetSdk = version["targetSdk"] ?: ""
    }
}
