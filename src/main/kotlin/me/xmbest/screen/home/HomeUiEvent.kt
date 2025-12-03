package me.xmbest.screen.home

sealed class HomeUiEvent {
    class InputKey(val key: Int) : HomeUiEvent()
    class ExecuteAction(val action: HomeAction) : HomeUiEvent()
    data object ShowStatusbar : HomeUiEvent()
    data object HideStatusbar : HomeUiEvent()
    data object Reboot : HomeUiEvent()
    data object OpenSettings : HomeUiEvent()
    data object OpenWifiAdb : HomeUiEvent()
    data object ScreenShot : HomeUiEvent()
    data object FindCurrentActivity : HomeUiEvent()
    data object ClearCurrentActivity : HomeUiEvent()
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