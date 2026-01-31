package me.xmbest.screen.customer

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.xmbest.appStorageAbsolutePath
import me.xmbest.base.BaseViewModel
import me.xmbest.cfg
import me.xmbest.ddmlib.DeviceOperate
import me.xmbest.ddmlib.Log
import me.xmbest.screen.customer.entity.*
import java.io.File

class CustomerViewModel : BaseViewModel<CustomerUiState>() {

    companion object {

        private const val TAG = "CustomerViewModel"

        // 类型映射
        private val typeMap = mapOf(
            FastBroadType.INPUT_SEND to InputSendData::class.java,
            FastBroadType.BUTTON_GROUP to ButtonGroupData::class.java,
            FastBroadType.SHELL_SEND to ShellSendData::class.java
        )
    }

    override val _uiState: MutableStateFlow<CustomerUiState> = MutableStateFlow(CustomerUiState())

    init {
        loadConfig()
    }

    fun onEvent(event: CustomerUiEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is CustomerUiEvent.Config -> handleConfigEvent(event)
                is CustomerUiEvent.Command -> handleCommandEvent(event)
                is CustomerUiEvent.UI -> handleUIEvent(event)
            }
        }
    }

    private suspend fun handleConfigEvent(event: CustomerUiEvent.Config) {
        when (event) {
            is CustomerUiEvent.Config.Refresh -> loadConfig()
            is CustomerUiEvent.Config.Export -> handleExportConfig()
            is CustomerUiEvent.Config.Import -> handleImportConfig()
        }
    }

    private suspend fun handleCommandEvent(event: CustomerUiEvent.Command) {
        when (event) {
            is CustomerUiEvent.Command.Execute -> handleExecuteCommand(event.cmd)
        }
    }

    private fun handleUIEvent(event: CustomerUiEvent.UI) {
        when (event) {
            is CustomerUiEvent.UI.UpdateInputValue -> handleUpdateInputValue(event.uuid, event.value)
            is CustomerUiEvent.UI.Toast -> handleToast(event.message)
        }
    }

    private fun loadConfig() {
        var toastMessage = ""
        val configList = runCatching {
            val gson = Gson()
            val configFile = File(appStorageAbsolutePath, cfg.second)
            if (!configFile.exists()) {
                return@runCatching getDefaultConfig()
            }

            val configJson = configFile.readText()
            val configList = mutableListOf<BaseFastBroadData>()
            Log.d(TAG, "configJson = $configJson")
            val listMap: List<Map<String, Any>> =
                gson.fromJson(configJson, object : TypeToken<List<Map<String, Any>>>() {}.type)
            listMap.forEach {
                configList.add(
                    gson.fromJson(
                        gson.toJson(it),
                        typeMap[it["type"]]
                    )
                )
            }
            if (configList.isEmpty()) {
                toastMessage = getString("customer.config.empty")
            }
            configList
        }.onFailure {
            Log.e(TAG, "Error: ${it.message}")
            toastMessage = getString("customer.config.load.failed")
        }.getOrNull() ?: getDefaultConfig()

        _uiState.value = _uiState.value.copy(
            configList = configList,
            toast = toastMessage
        )
        Log.e(TAG, "loadConfig finished, toast: $toastMessage")
    }

    private suspend fun handleExportConfig() {
        withContext(Dispatchers.IO) {
            try {
                val configFile = File(appStorageAbsolutePath, cfg.second)
                if (!configFile.exists()) {
                    _uiState.value = _uiState.value.copy(toast = getString("customer.export.no.config"))
                    return@withContext
                }

                val file = FileKit.openFileSaver(
                    extension = "json",
                    suggestedName = "config"
                )

                if (file != null) {
                    val configContent = configFile.readText()
                    File(file.path).writeText(configContent)
                    _uiState.value = _uiState.value.copy(toast = getString("customer.export.success"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(toast = "${getString("customer.export.failed")}: ${e.message}")
            }
        }
    }

    private suspend fun handleImportConfig() {
        withContext(Dispatchers.IO) {
            try {
                val file = FileKit.openFilePicker(
                    title = getString("customer.import.title")
                ) ?: return@withContext

                val configJson = File(file.path).readText()

                // Validate and parse config
                val validationResult = validateConfigJson(configJson)
                if (!validationResult.isValid) {
                    _uiState.value = _uiState.value.copy(toast = validationResult.errorMessage!!)
                    return@withContext
                }

                saveConfig(configJson)
                _uiState.value = _uiState.value.copy(toast = getString("customer.import.success"))

            } catch (e: Exception) {
                Log.e(TAG, "Import config failed: ${e.message}")
                _uiState.value = _uiState.value.copy(toast = "${getString("customer.import.failed")}: ${e.message}")
            }
        }
    }

    private fun validateConfigJson(configJson: String): ValidationResult {
        return try {
            val gson = Gson()
            val listMap: List<Map<String, Any>> =
                gson.fromJson(configJson, object : TypeToken<List<Map<String, Any>>>() {}.type)

            if (listMap.isEmpty()) {
                return ValidationResult(false, getString("customer.import.empty"))
            }

            // Validate each item's type and structure
            val parsedList = listMap.mapIndexed { index, itemMap ->
                val type = itemMap["type"]
                val dataClass = typeMap[type]
                    ?: return ValidationResult(false, getString("customer.import.invalid.type"))

                try {
                    gson.fromJson(gson.toJson(itemMap), dataClass)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse item at index $index: ${e.message}")
                    return ValidationResult(false, getString("customer.import.invalid.format"))
                }
            }

            if (parsedList.isEmpty()) {
                ValidationResult(false, getString("customer.import.empty"))
            } else {
                ValidationResult(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Config validation failed: ${e.message}")
            ValidationResult(false, getString("customer.import.invalid.format"))
        }
    }

    private data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    private fun saveConfig(str: String) {
        val configFile = File(appStorageAbsolutePath, cfg.second)
        configFile.writeText(str)
        loadConfig()
    }

    private suspend fun handleExecuteCommand(cmd: String) {
        withContext(Dispatchers.IO) {
            try {
                DeviceOperate.shell(cmd.replaceFirst("adb shell", ""))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(toast = "${getString("customer.command.failed")}: ${e.message}")
            }
        }
    }

    private fun handleUpdateInputValue(uuid: String?, value: String) {

    }

    private fun handleToast(message: String) {
        _uiState.value = _uiState.value.copy(toast = message)
    }

    private fun getDefaultConfig(): List<BaseFastBroadData> {
        return listOf(
            InputSendData(
                title = getString("customer.default.textInput.title"),
                cmd = "input keyboard text \"{action}\"",
                template = "{action}",
                hint = getString("customer.default.textInput.hint"),
                btnText = getString("customer.default.textInput.btn")
            ),
            ButtonGroupData(
                title = getString("customer.default.systemSettings.title"),
                list = listOf(
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.home"),
                        cmd = "adb shell am start com.android.settings/com.android.settings.Settings"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.wifi"),
                        cmd = "adb shell am start -a android.settings.WIFI_SETTINGS"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.bluetooth"),
                        cmd = "adb shell am start -a android.settings.BLUETOOTH_SETTINGS"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.notification"),
                        cmd = "adb shell am start -a android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.systemSettings.accessibility"),
                        cmd = "adb shell am start -a android.settings.ACCESSIBILITY_SETTINGS"
                    )
                )
            ),
            ButtonGroupData(
                title = getString("customer.default.displaySettings.title"),
                list = listOf(
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.pointerOn"),
                        cmd = "adb shell settings put system pointer_location 1"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.pointerOff"),
                        cmd = "adb shell settings put system pointer_location 0"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.touchOn"),
                        cmd = "adb shell settings put system show_touches 1"
                    ),
                    ButtonData(
                        btnText = getString("customer.default.displaySettings.touchOff"),
                        cmd = "adb shell settings put system show_touches 0"
                    ),
                )
            ),
            ShellSendData(
                title = getString("customer.default.shell.title"),
                btnText = getString("customer.default.shell.btn"),
                hintText = getString("customer.default.shell.hint"),
                minHeight = 270
            )
        )
    }
}
