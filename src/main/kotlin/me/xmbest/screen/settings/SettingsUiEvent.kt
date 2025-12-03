package me.xmbest.screen.settings

import me.xmbest.model.Environment
import me.xmbest.model.Theme

sealed class SettingsUiEvent {
    class UpdateTheme(val theme: Theme) : SettingsUiEvent()
    class UpdateAdbEnv(val environment: Environment) : SettingsUiEvent()
    data object UpdateCustomerAdb : SettingsUiEvent()
    data object ClearData : SettingsUiEvent()
}