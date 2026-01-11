package me.xmbest.screen.settings

import me.xmbest.model.Environment
import me.xmbest.model.Theme

sealed class SettingsUiEvent {
    class UpdateTheme(val theme: Theme) : SettingsUiEvent()
    class UpdateAdbEnv(val environment: Environment) : SettingsUiEvent()
    class UpdateScreenshotSaveEnabled(val enabled: Boolean) : SettingsUiEvent()
    class UpdateCmdAutoCloseEnabled(val enabled: Boolean) : SettingsUiEvent()
    class UpdateCmdAutoCloseTimeout(val seconds: Int) : SettingsUiEvent()
    data object UpdateCustomerAdb : SettingsUiEvent()
    data object UpdateScreenshotSavePath : SettingsUiEvent()
    data object ClearData : SettingsUiEvent()
}
