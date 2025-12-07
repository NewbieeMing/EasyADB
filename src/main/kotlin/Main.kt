import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.xmbest.Config
import me.xmbest.model.Theme
import me.xmbest.module.InitModule
import me.xmbest.screen.navigation.NaviScreen

@OptIn(InternalComposeUiApi::class)
@Composable
@Preview
fun App() {

    val theme = Config.theme.collectAsState().value

    MaterialTheme(
        colors =
            if (theme == Theme.System)
                if (isSystemInDarkTheme()) Theme.Night.color else Theme.Light.color
            else
                Config.theme.value.color
    ) {
        NaviScreen()
    }
}

fun main() = application {
    InitModule.init()
    val windowState = Config.windowState.collectAsState()
    Window(title = "EasyADB", onCloseRequest = ::exitApplication, state = windowState.value, icon = painterResource("icon/logo.ico")) {
        App()
    }
}