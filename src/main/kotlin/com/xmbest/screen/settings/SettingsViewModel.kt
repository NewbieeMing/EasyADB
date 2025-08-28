package com.xmbest.screen.settings

import androidx.lifecycle.viewModelScope
import com.xmbest.Config
import com.xmbest.base.BaseViewModel
import com.xmbest.ddmlib.DeviceManager
import com.xmbest.model.Environment
import com.xmbest.model.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel<SettingsUiState>() {

    val themeList = listOf(
        Pair(getString("theme.light"), Theme.Light),
        Pair(getString("theme.night"), Theme.Dark),
        Pair(getString("theme.system"), Theme.System)
    )

    val envList = listOf(
        Pair(getString("env.system"), Environment.Program),
        Pair(getString("env.program"), Environment.System),
        Pair(getString("env.custom"), Environment.Custom(""))
    )

    override val _uiState =
        MutableStateFlow(
            SettingsUiState(
                DeviceManager.adbPath.value,
                Config.theme.value
            )
        )

    init {
        viewModelScope.launch(Dispatchers.Default) {
            Config.theme.collectLatest { newTheme ->
                _uiState.value = _uiState.value.copy(theme = newTheme)
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            DeviceManager.adbPath.collectLatest { path ->
                _uiState.value = _uiState.value.copy(adbPAth = path)
            }
        }
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.UpdateTheme -> changeTheme(event.theme)
            is SettingsUiEvent.UpdateAdbEnv -> changeAdbEnv(event.environment)
        }
    }

    private fun changeAdbEnv(environment: Environment) {
        viewModelScope.launch(Dispatchers.IO) {
            DeviceManager.initialize(environment.path)
        }
    }

    private fun changeTheme(newTheme: Theme) {
        viewModelScope.launch(Dispatchers.Default) {
            Config.changeTheme(newTheme)
        }
    }

}