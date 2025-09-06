package me.xmbest.screen.settings

import me.xmbest.model.Environment
import me.xmbest.model.Theme

sealed class SettingsUiEvent {
    class UpdateTheme(val theme: Theme) : SettingsUiEvent()
    class UpdateAdbEnv(val environment: Environment) : SettingsUiEvent()
    object UpdateCustomerAdb : SettingsUiEvent()
    object ClearData : SettingsUiEvent()
}