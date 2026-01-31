package me.xmbest.screen.settings

import me.xmbest.model.Environment
import me.xmbest.model.Theme

sealed class SettingsUiEvent {
    // 主题设置相关事件
    sealed class ThemeSettings : SettingsUiEvent() {
        data class UpdateTheme(val theme: Theme) : ThemeSettings()
    }

    // ADB环境设置相关事件
    sealed class AdbSettings : SettingsUiEvent() {
        data class UpdateAdbEnv(val environment: Environment) : AdbSettings()
        data object UpdateCustomerAdb : AdbSettings()
    }

    // 截图设置相关事件
    sealed class ScreenshotSettings : SettingsUiEvent() {
        data class UpdateScreenshotSaveEnabled(val enabled: Boolean) : ScreenshotSettings()
        data object UpdateScreenshotSavePath : ScreenshotSettings()
    }

    // 终端设置相关事件
    sealed class TerminalSettings : SettingsUiEvent() {
        data class UpdateCmdAutoCloseEnabled(val enabled: Boolean) : TerminalSettings()
        data class UpdateCmdAutoCloseTimeout(val seconds: Int) : TerminalSettings()
    }

    // 数据管理相关事件
    sealed class DataManagement : SettingsUiEvent() {
        data object ClearData : DataManagement()
    }
}
