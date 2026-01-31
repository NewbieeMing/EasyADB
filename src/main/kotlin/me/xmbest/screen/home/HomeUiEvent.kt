package me.xmbest.screen.home

sealed class HomeUiEvent {
    // 设备控制相关事件
    sealed class DeviceControl : HomeUiEvent() {
        data class InputKey(val key: Int) : DeviceControl()
        data object Reboot : DeviceControl()
        data object ShowStatusbar : DeviceControl()
        data object HideStatusbar : DeviceControl()
    }

    // 应用操作相关事件
    sealed class AppOperation : HomeUiEvent() {
        data object FindCurrentActivity : AppOperation()
        data object ClearCurrentActivity : AppOperation()
    }

    // 系统功能相关事件
    sealed class SystemFeature : HomeUiEvent() {
        data object OpenSettings : SystemFeature()
        data object OpenWifiAdb : SystemFeature()
        data object ScreenShot : SystemFeature()
    }

    // 通用操作事件（用于统一处理多种操作）
    sealed class Action : HomeUiEvent() {
        data class ExecuteAction(val action: HomeAction) : Action()
    }
}

enum class HomeAction {
    CURRENT_ACTIVITY,
    REBOOT,
    SCREENSHOT,
    WIFI_ADB,
    NATIVE_SETTINGS,
    CLEAR_LOGCAT,
    SHOW_STATUSBAR,
    HIDE_STATUSBAR
}
