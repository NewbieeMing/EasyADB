package me.xmbest.screen.settings

import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.xmbest.Config
import me.xmbest.base.BaseViewModel
import me.xmbest.customerAdbAbsolutePath
import me.xmbest.ddmlib.DeviceManager
import me.xmbest.model.Environment
import me.xmbest.model.Theme
import me.xmbest.screenshotSaveAbsolutePath
import me.xmbest.util.PreferencesUtil
import me.xmbest.util.PreferencesUtil.PREFERENCES_ADB_PATH
import me.xmbest.util.PreferencesUtil.PREFERENCES_CUSTOMER_ADB_PATH
import me.xmbest.util.PreferencesUtil.PREFERENCES_SCREENSHOT_SAVE_ENABLED
import me.xmbest.util.PreferencesUtil.PREFERENCES_SCREENSHOT_SAVE_PATH
import me.xmbest.util.PreferencesUtil.PREFERENCES_THEME
import org.jetbrains.skiko.hostOs
import kotlin.system.exitProcess

class SettingsViewModel : BaseViewModel<SettingsUiState>() {


    override val _uiState =
        MutableStateFlow(
            SettingsUiState(
                DeviceManager.adbPath.value,
                customerAdbAbsolutePath,
                Config.theme.value,
                PreferencesUtil.get(PREFERENCES_SCREENSHOT_SAVE_ENABLED, false),
                screenshotSaveAbsolutePath
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
                _uiState.value = _uiState.value.copy(adbPath = path)
            }
        }
    }

    fun onEvent(event: SettingsUiEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is SettingsUiEvent.UpdateTheme -> changeTheme(event.theme)
                is SettingsUiEvent.UpdateAdbEnv -> changeAdbEnv(event.environment)
                is SettingsUiEvent.UpdateScreenshotSaveEnabled -> changeScreenshotSaveEnabled(event.enabled)
                is SettingsUiEvent.UpdateCustomerAdb -> changeCustomerAdb()
                is SettingsUiEvent.UpdateScreenshotSavePath -> changeScreenshotSavePath()
                is SettingsUiEvent.ClearData -> clearData()
            }
        }
    }

    private suspend fun changeAdbEnv(environment: Environment) {
        if (DeviceManager.adbPath.value == environment.path) return
        if (environment is Environment.Custom && customerAdbAbsolutePath.isEmpty()) {
            changeCustomerAdb()?.let {
                environment.path = it
                _uiState.value = _uiState.value.copy(customerAdbPath = customerAdbAbsolutePath)
            } ?: return
        }

        withContext(Dispatchers.IO) {
            PreferencesUtil.set(PREFERENCES_ADB_PATH, environment.path)
            DeviceManager.initialize(environment.path)
        }
    }

    private suspend fun changeCustomerAdb(): String? {
        val filePicker = FileKit.openFilePicker(
            title = getString("settings.adb.selectExecutable"),
            mode = FileKitMode.Single,
            type = FileKitType.File(if (hostOs.isWindows) "exe" else "")
        )

        return filePicker?.let {
            val path = it.absolutePath()
            PreferencesUtil.set(PREFERENCES_CUSTOMER_ADB_PATH, path)
            _uiState.value = _uiState.value.copy(customerAdbPath = customerAdbAbsolutePath)
            path
        }
    }

    private fun changeTheme(newTheme: Theme) {
        viewModelScope.launch(Dispatchers.Default) {
            PreferencesUtil.set(PREFERENCES_THEME, newTheme.label)
            Config.changeTheme(newTheme)
        }
    }

    private fun changeScreenshotSaveEnabled(enabled: Boolean) {
        PreferencesUtil.set(PREFERENCES_SCREENSHOT_SAVE_ENABLED, enabled)
        _uiState.value = _uiState.value.copy(screenshotSaveEnabled = enabled)
    }

    private suspend fun changeScreenshotSavePath() {
        val directory = FileKit.openDirectoryPicker(
            title = getString("settings.screenshot.save.select")
        ) ?: return
        val path = directory.path
        PreferencesUtil.set(PREFERENCES_SCREENSHOT_SAVE_PATH, path)
        _uiState.value = _uiState.value.copy(screenshotSavePath = path)
    }

    private fun clearData() {
        PreferencesUtil.clear()
        exitProcess(0)
    }

}
