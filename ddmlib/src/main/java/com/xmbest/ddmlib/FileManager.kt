package com.xmbest.ddmlib

import com.android.ddmlib.FileListingService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object FileManager {
    private const val TAG = "FileManager"
    private val coroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName(TAG))
    private val _fileListingService = MutableStateFlow<FileListingService?>(null)
    val fileListingService = _fileListingService.asStateFlow()

    init {
        coroutineScope.launch {
            DeviceManager.device.collect { device ->
                _fileListingService.update {
                    device?.let { FileListingService(device) }
                }
            }
        }
    }
}