package com.xmbest.screen.file

sealed class FileUiEvent(val path: String) {
    object Refresh : FileUiEvent("")
    class NavigateToPath(path: String) : FileUiEvent(path)
}