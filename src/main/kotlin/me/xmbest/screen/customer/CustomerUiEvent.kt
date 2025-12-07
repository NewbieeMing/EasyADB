package me.xmbest.screen.customer

sealed class CustomerUiEvent {
    data object Refresh : CustomerUiEvent()
    data object ExportConfig : CustomerUiEvent()
    data object ImportConfig : CustomerUiEvent()
    data class ExecuteCommand(val cmd: String) : CustomerUiEvent()
    data class UpdateInputValue(val uuid: String, val value: String) : CustomerUiEvent()
    data class Toast(val message: String) : CustomerUiEvent()
}
