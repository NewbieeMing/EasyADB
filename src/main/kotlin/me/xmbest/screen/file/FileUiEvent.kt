package me.xmbest.screen.file

import com.android.ddmlib.FileListingService

sealed class FileUiEvent {
    // 导航相关事件
    sealed class Navigation : FileUiEvent() {
        data object Refresh : Navigation()
        data class NavigateToPath(val path: String) : Navigation()
        data object JumpToClipboardPath : Navigation()
        data class NavigateToFavorite(val favoritePath: String) : Navigation()
    }

    // 文件操作相关事件
    sealed class FileOperation : FileUiEvent() {
        data object Imported : FileOperation()
        data class UploadFiles(val files: List<String>) : FileOperation()
        data class DownloadFiles(val files: List<FileListingService.FileEntry>) : FileOperation()
        data class DeleteFiles(val files: List<FileListingService.FileEntry>) : FileOperation()
        data object DeleteAllFiles : FileOperation()
        data class CreateFolder(val folderName: String) : FileOperation()
        data class CreateFile(val fileName: String) : FileOperation()
        data class RenameFile(val oldPath: String, val newName: String) : FileOperation()
    }

    // 拖拽相关事件
    sealed class Drag : FileUiEvent() {
        data object StartDrag : Drag()
        data object DragEnd : Drag()
    }

    // 收藏夹相关事件
    sealed class Favorites : FileUiEvent() {
        data class ToggleFavorite(val filePath: String) : Favorites()
        data object RefreshFavorites : Favorites()
    }

    // UI交互相关事件
    sealed class UI : FileUiEvent() {
        data class Toast(val message: String) : UI()
        data class UpdateFilter(val filter: String) : UI()
    }
}