package com.xmbest.screen.file

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.android.ddmlib.FileListingService
import com.xmbest.FILE_SPLIT
import com.xmbest.base.BaseViewModel
import com.xmbest.ddmlib.DeviceOperate
import com.xmbest.ddmlib.FileManager
import com.xmbest.ddmlib.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class FileViewModel : BaseViewModel<FileUiState>() {

    companion object {
        private const val TAG = "FileViewModel"
    }

    override val _uiState: MutableStateFlow<FileUiState> = MutableStateFlow(FileUiState())

    init {
        viewModelScope.launch(Dispatchers.Default) {
            FileManager.fileListingService.filter { it != null }.collect {
                _uiState.value =
                    _uiState.value.copy(
                        children = DeviceOperate.ls(uiState.value.parentPath)
                    )
            }
        }
    }

    fun onEvent(event: FileUiEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is FileUiEvent.NavigateToPath -> navigateToPath(event.path)
                is FileUiEvent.Refresh -> refreshCurrentDirectory()
            }
        }

    }

    fun calculatePath(parentPath: String, fileName: String): String {
        return if (parentPath.endsWith(FILE_SPLIT)) {
            parentPath + fileName
        } else {
            "$parentPath$FILE_SPLIT$fileName"
        }
    }

    private suspend fun refreshCurrentDirectory() {
        Log.d(TAG, "refreshCurrentDirectory path = ${uiState.value.parentPath}")
        _uiState.value =
            _uiState.value.copy(
                children = DeviceOperate.ls(uiState.value.parentPath)
            )
    }

    private suspend fun navigateToPath(path: String) {
        Log.d(TAG, "navigateToPath path = $path")
        _uiState.value = _uiState.value.copy(parentPath = path)
        refreshCurrentDirectory()
    }


    fun getFileTypeInfo(type: Int): FileTypeInfo {
        return when (type) {
            FileListingService.TYPE_DIRECTORY -> FileTypeInfo(
                Icons.Default.Folder,
                getString("file.directory")
            )

            FileListingService.TYPE_DIRECTORY_LINK -> FileTypeInfo(
                Icons.Default.FolderOpen,
                getString("file.directoryLink")
            )

            FileListingService.TYPE_BLOCK -> FileTypeInfo(
                Icons.Default.Storage,
                getString("file.block")
            )

            FileListingService.TYPE_CHARACTER -> FileTypeInfo(
                Icons.Default.DeviceHub,
                getString("file.character")
            )

            FileListingService.TYPE_LINK -> FileTypeInfo(Icons.Default.Link, getString("file.link"))
            FileListingService.TYPE_SOCKET -> FileTypeInfo(
                Icons.Default.Cable,
                getString("file.socket")
            )

            FileListingService.TYPE_FIFO -> FileTypeInfo(
                Icons.Default.Timeline,
                getString("file.fifo")
            )

            FileListingService.TYPE_FILE -> FileTypeInfo(
                Icons.AutoMirrored.Filled.InsertDriveFile,
                getString("file.file")
            )

            else -> FileTypeInfo(Icons.AutoMirrored.Filled.Help, getString("file.other"))
        }
    }


    /**
     * 字节转gb单位
     * @param size 字节数大小
     */
    fun byte2Gb(size: String): String {
        var sizeInt = 0

        if (size.contains(",")) {
            val split = size.split(",")
            if (split.size > 1) {
                val sizeInner = split[1].trim()
                try {
                    sizeInt = sizeInner.toInt()
                } catch (e: Exception) {
                    return "0B"
                }
            }
        }

        try {
            sizeInt = size.toInt()
        } catch (e: Exception) {
            return "0B"
        }
        //获取到的size为：1705230
        val GB = 1024 * 1024 * 1024 //定义GB的计算常量
        val MB = 1024 * 1024 //定义MB的计算常量
        val KB = 1024 //定义KB的计算常量
        val df = DecimalFormat("0.00") //格式化小数
        var resultSize = ""
        resultSize = if (sizeInt / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            df.format((sizeInt / GB.toFloat()).toDouble()) + "GB"
        } else if (sizeInt / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            df.format((sizeInt / MB.toFloat()).toDouble()) + "MB"
        } else if (sizeInt / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            df.format((sizeInt / KB.toFloat()).toDouble()) + "KB"
        } else {
            size + "B"
        }
        return resultSize
    }
}

data class FileTypeInfo(
    val icon: ImageVector,
    val text: String
)