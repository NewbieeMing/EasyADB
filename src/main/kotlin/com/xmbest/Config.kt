package com.xmbest

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.xmbest.model.Theme
import com.xmbest.theme.Blue
import com.xmbest.theme.Night
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

object Config {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    const val STRINGS_NAME = "strings"

    val lightColors = Blue

    val darkColors = Night

    private val _windowState = MutableStateFlow(
        WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1280.dp, 720.dp)
        )
    )

    private val _locale = MutableStateFlow(Locale.CHINA)
    val locale = _locale.asStateFlow()

    val isDart = MutableStateFlow<Boolean?>(null)

    val windowState = _windowState.asStateFlow()

    private val _theme = MutableStateFlow(Theme.System)

    val theme = _theme.asStateFlow()


    init {
        coroutineScope.launch {
            theme.collectLatest { t ->
                isDart.update {
                    when (t) {
                        Theme.Dark -> true
                        Theme.Light -> false
                        else -> null
                    }
                }
            }
        }
    }

    fun changeTheme(newTheme: Theme) {
        _theme.update { newTheme }
    }

}