package me.xmbest.screen.customer

sealed class CustomerUiEvent {
    // 配置管理相关事件
    sealed class Config : CustomerUiEvent() {
        data object Refresh : Config()
        data object Export : Config()
        data object Import : Config()
    }

    // 命令执行相关事件
    sealed class Command : CustomerUiEvent() {
        data class Execute(val cmd: String) : Command()
    }

    // UI交互相关事件
    sealed class UI : CustomerUiEvent() {
        data class UpdateInputValue(val uuid: String?, val value: String) : UI()
        data class Toast(val message: String) : UI()
    }
}
