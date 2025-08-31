package com.xmbest.screen.file

import com.android.ddmlib.FileListingService

data class FileUiState(
    val parentPath: String = "/sdcard",
    val children: List<FileListingService.FileEntry> = emptyList(),
    val isDragging: Boolean = false,  // 是否处于拖拽状态
    val uploadTipText: String = "", // 上传提示文本
    val toast: String = "",
)