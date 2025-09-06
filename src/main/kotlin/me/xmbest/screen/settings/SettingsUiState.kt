package me.xmbest.screen.settings

import me.xmbest.model.Theme

data class SettingsUiState(
    val adbPath: String,
    val customerAdbPath: String,
    val theme: Theme,
)