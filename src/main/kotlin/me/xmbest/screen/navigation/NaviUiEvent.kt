package me.xmbest.screen.navigation

import com.android.ddmlib.IDevice

sealed class NaviUiEvent {
    // 导航相关事件
    sealed class Navigation : NaviUiEvent() {
        data class SelectLeftItem(val index: Int) : Navigation()
    }

    // 设备管理相关事件
    sealed class DeviceManagement : NaviUiEvent() {
        data class SelectDevice(val device: IDevice) : DeviceManagement()
        data class ShowDeviceList(val show: Boolean) : DeviceManagement()
        data object RefreshDevice : DeviceManagement()
    }
}
