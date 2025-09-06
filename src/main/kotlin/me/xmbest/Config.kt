package me.xmbest

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.xmbest.locale.PropertiesLocalization
import me.xmbest.model.Environment
import me.xmbest.model.Theme
import me.xmbest.theme.blue
import me.xmbest.theme.purple
import me.xmbest.util.PreferencesUtil
import me.xmbest.util.PreferencesUtil.PREFERENCES_THEME
import java.util.*

object Config {
    const val STRINGS_NAME = "strings"

    private val _locale = MutableStateFlow(Locale.CHINA)
    val locale = _locale.asStateFlow()

    private val strings = PropertiesLocalization.create(STRINGS_NAME)

    val themeList = listOf(
        Theme.System,
        Theme.Light,
        Theme.Night,
        Theme.Other(strings.get("theme.blue"), blue),
        Theme.Other(strings.get("theme.purple"), purple)
    )

    val envList = listOf(
        Pair(strings.get("env.system"), Environment.System),
        Pair(strings.get("env.program"), Environment.Program),
        Pair(strings.get("env.custom"), Environment.Custom)
    )

    private val _windowState = MutableStateFlow(
        WindowState(
            position = WindowPosition.Aligned(Alignment.Center), size = DpSize(1280.dp, 720.dp)
        )
    )

    val windowState = _windowState.asStateFlow()

    private val currentTheme = themeList.firstOrNull {
        it.label == PreferencesUtil.get(PREFERENCES_THEME, Theme.System.label)
    } ?: Theme.System

    private val _theme = MutableStateFlow(currentTheme)

    val theme = _theme.asStateFlow()


    fun changeTheme(newTheme: Theme) {
        _theme.update { newTheme }
    }

}