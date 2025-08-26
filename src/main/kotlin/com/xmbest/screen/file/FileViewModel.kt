package com.xmbest.screen.file

import androidx.lifecycle.viewModelScope
import com.xmbest.base.BaseViewModel
import com.xmbest.ddmlib.DeviceOperate
import com.xmbest.ddmlib.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class FileViewModel : BaseViewModel<FileUiState>() {
    override val _uiState: MutableStateFlow<FileUiState> = MutableStateFlow(FileUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            FileManager.fileListingService.filter { it != null }.collect {
                _uiState.value =
                    _uiState.value.copy(
                        children = DeviceOperate.ls(uiState.value.parentPath)
                    )
            }
        }
    }

    fun onEvent(event: FileUiEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is FileUiEvent.NavigateToPath -> navigateToPath(event.path)
                is FileUiEvent.Refresh -> refreshCurrentDirectory()
            }
        }

    }

    private suspend fun refreshCurrentDirectory() {
        _uiState.value =
            _uiState.value.copy(
                children = DeviceOperate.ls(uiState.value.parentPath)
            )
    }

    private suspend fun navigateToPath(path: String) {
        _uiState.value = _uiState.value.copy(parentPath = path)
        refreshCurrentDirectory()
    }
}