package me.xmbest.screen.app

sealed class AppUiEvent {
    // UI设置相关事件
    sealed class Settings : AppUiEvent() {
        class ChangeFilter(val filter: String?) : Settings()
        class ChangeAppMode(val mode: AppShowMode) : Settings()
        data object ChangeAuto : Settings()
        data object ChangeThird : Settings()
    }

    // 生命周期事件
    sealed class Lifecycle : AppUiEvent() {
        data object Show : Lifecycle()
        data object Dispose : Lifecycle()
    }

    // 应用操作事件
    sealed class AppOperation : AppUiEvent() {
        class Kill(val pids: List<String>) : AppOperation()
        class ForceStop(val applicationId: String) : AppOperation()
        class StartApp(val packageName: String) : AppOperation()
        class ClearData(val packageName: String) : AppOperation()
        class Uninstall(val packageName: String) : AppOperation()
    }
}