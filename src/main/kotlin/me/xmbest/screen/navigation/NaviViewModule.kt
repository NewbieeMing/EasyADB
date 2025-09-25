package me.xmbest.screen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.lifecycle.viewModelScope
import com.android.ddmlib.IDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.xmbest.base.BaseViewModel
import me.xmbest.ddmlib.DeviceManager
import me.xmbest.model.Page
import me.xmbest.screen.app.AppScreen
import me.xmbest.screen.file.FileScreen
import me.xmbest.screen.home.HomeScreen
import me.xmbest.screen.settings.SettingsScreen

class NaviViewModule() : BaseViewModel<NaviUiState>() {
    val pageList = listOf(
        Page(
            name = getString("router.item.commonFeatures"),
            Icons.Outlined.Category
        ) {
            HomeScreen()
        },
        Page(
            "应用管理",
            Icons.Outlined.GridView
        ) {
            AppScreen()
        },
        Page(
            getString("router.item.fileManagement"),
            Icons.Outlined.FolderOpen
        ) {
            FileScreen()
        },
        Page(
            getString("router.item.settings"),
            Icons.Outlined.Settings
        ) { SettingsScreen() }
    )

    override val _uiState = MutableStateFlow(NaviUiState())

    init {
        viewModelScope.launch(Dispatchers.Default) {
            DeviceManager.device.collectLatest {
                _uiState.value = _uiState.value.copy(device = it)
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            DeviceManager.devices.collectLatest {
                _uiState.value = _uiState.value.copy(devices = it)
            }
        }
    }

    fun onEvent(event: NaviUiEvent) {
        when (event) {
            is NaviUiEvent.SelectLeftItem -> selectLeftItem(event.index)
            is NaviUiEvent.SelectDevice -> selectDevice(event.device)
            is NaviUiEvent.ShowDeviceList -> showDeviceList(event.show)
            is NaviUiEvent.RefreshDevice -> refreshDevice()
        }
    }

    private fun selectLeftItem(pageIndex: Int) {
        _uiState.value = _uiState.value.copy(index = pageIndex)
    }

    private fun showDeviceList(show: Boolean) {
        _uiState.value = _uiState.value.copy(devicesListShow = show)
    }

    private fun selectDevice(iDevice: IDevice) {
        DeviceManager.changeDevice(iDevice)
        showDeviceList(false)
    }

    private fun refreshDevice() {
        DeviceManager.refreshDevices()
    }

}