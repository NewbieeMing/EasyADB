package me.xmbest.screen.app

sealed class AppUiEvent {
    class ChangeFilter(val filter: String?) : AppUiEvent()
    class ChangeAppMode(val mode: AppShowMode) : AppUiEvent()
    data object ChangeAuto : AppUiEvent()
    data object ChangeThird : AppUiEvent()
    data object Show : AppUiEvent()
    data object Dispose : AppUiEvent()
    class Kill(val pids: List<String>) : AppUiEvent()
    class ForceStop(val applicationId: String) : AppUiEvent()
    class StartApp(val packageName: String) : AppUiEvent()
    class ClearData(val packageName: String) : AppUiEvent()
    class Uninstall(val packageName: String) :AppUiEvent()
}