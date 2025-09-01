package com.xmbest.screen.file

import com.android.ddmlib.FileListingService

sealed class FileUiEvent(val path: String) {
    object Refresh : FileUiEvent("")
    class NavigateToPath(path: String) : FileUiEvent(path)

    /**
     * 开始拖拽
     * @param files 拖拽的文件列表
     * @param subPath 目标路径，不传为当前路径，传则当前路径[subPath]
     */
    class StartDrag(val files: List<String>) : FileUiEvent("")
    object DragEnd : FileUiEvent("")
    class UploadFiles(val files: List<String>) : FileUiEvent("")
    class DownloadFiles(val files: List<FileListingService.FileEntry>) : FileUiEvent("")
    object Imported : FileUiEvent("")
    class Toast(val message: String) : FileUiEvent("")
}