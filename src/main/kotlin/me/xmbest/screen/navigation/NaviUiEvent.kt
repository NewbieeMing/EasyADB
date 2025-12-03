package me.xmbest.screen.navigation

import com.android.ddmlib.IDevice

sealed class NaviUiEvent {
    class SelectLeftItem(val index: Int) : NaviUiEvent()
    class SelectDevice(val device: IDevice) : NaviUiEvent()
    class ShowDeviceList(val show: Boolean) : NaviUiEvent()
    data object RefreshDevice : NaviUiEvent()
}