package com.xmbest.screen.file

import com.android.ddmlib.FileListingService

data class FileUiState(
    val parentPath: String = "/sdcard",
    val children: List<FileListingService.FileEntry> = emptyList()
)