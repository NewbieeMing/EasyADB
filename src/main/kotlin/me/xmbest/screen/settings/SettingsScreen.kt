package me.xmbest.screen.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.xmbest.Config
import me.xmbest.LocalDialogState
import me.xmbest.locale.PropertiesLocalization
import me.xmbest.model.Environment
import me.xmbest.model.Theme
import me.xmbest.util.DialogUtil
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState = viewModel.uiState.collectAsState().value
    val dialogState = LocalDialogState.current
    val windowState = Config.windowState.collectAsState().value
    var windowMode by remember { mutableStateOf(Config.getWindowSizeMode()) }
    val savedCustomWindowSize = Config.getCustomWindowSizeDp()
    var customWidth by remember { mutableStateOf(savedCustomWindowSize.width.value.roundToInt().toString()) }
    var customHeight by remember { mutableStateOf(savedCustomWindowSize.height.value.roundToInt().toString()) }
    val applyCustomWindowSize = {
        val width = customWidth.toIntOrNull()
        val height = customHeight.toIntOrNull()
        if (width != null && width > 0 && height != null && height > 0) {
            val sizeDp = DpSize(width.dp, height.dp)
            Config.saveCustomWindowSizeDp(sizeDp)
            Config.updateWindowSize(sizeDp)
        }
    }

    LaunchedEffect(uiState.customerAdbPath) {
        if (uiState.customerAdbPath != Environment.Custom.path) {
            viewModel.onEvent(SettingsUiEvent.UpdateAdbEnv(Environment.Custom))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ThemeSettingsSection(
            title = viewModel.getString("theme.setting"),
            themeList = Config.themeList,
            selectedTheme = uiState.theme,
            onThemeSelected = { viewModel.onEvent(SettingsUiEvent.UpdateTheme(it)) }
        )

        AdbConfigSection(
            title = viewModel.getString("adb.config"),
            envList = Config.envList,
            selectedPath = uiState.adbPath,
            customerPath = uiState.customerAdbPath,
            onEnvSelected = { viewModel.onEvent(SettingsUiEvent.UpdateAdbEnv(it)) },
            onCustomerChange = { viewModel.onEvent(SettingsUiEvent.UpdateCustomerAdb) }
        )

        WindowSizeSection(
            title = viewModel.getString("settings.window.size"),
            followLabel = viewModel.getString("settings.window.mode.follow"),
            rememberLabel = viewModel.getString("settings.window.mode.remember"),
            customLabel = viewModel.getString("settings.window.mode.custom"),
            widthLabel = viewModel.getString("settings.window.width"),
            heightLabel = viewModel.getString("settings.window.height"),
            applyLabel = viewModel.getString("settings.window.apply"),
            mode = windowMode,
            customWidth = customWidth,
            customHeight = customHeight,
            onModeChange = { mode ->
                windowMode = mode
                Config.setWindowSizeMode(mode)
                when (mode) {
                    Config.WindowSizeMode.Follow -> {
                        Config.updateWindowSize(Config.defaultWindowSizeDp)
                    }

                    Config.WindowSizeMode.Remember -> {
                        Config.saveRememberWindowSizeDp(windowState.size)
                    }

                    Config.WindowSizeMode.Custom -> {
                        applyCustomWindowSize()
                    }
                }
            },
            onCustomWidthChange = { value ->
                customWidth = value.filter { it.isDigit() }
            },
            onCustomHeightChange = { value ->
                customHeight = value.filter { it.isDigit() }
            },
            onApplyCustom = applyCustomWindowSize
        )

        LabeledSection(
            viewModel.getString("settings.other"),
            modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)
        ) {

            Column {
                // 截图保存
                ScreenshotSaveSection(
                    enableLabel = viewModel.getString("settings.screenshot.save.enable"),
                    pathLabel = viewModel.getString("settings.screenshot.save.path"),
                    enabled = uiState.screenshotSaveEnabled,
                    path = uiState.screenshotSavePath,
                    onEnabledChange = { enabled ->
                        viewModel.onEvent(SettingsUiEvent.UpdateScreenshotSaveEnabled(enabled))
                    },
                    onChangePath = { viewModel.onEvent(SettingsUiEvent.UpdateScreenshotSavePath) }
                )

                // 清除数据
                Button(
                    onClick = {
                        DialogUtil.showWarning(
                            dialogState = dialogState,
                            title = viewModel.getString("settings.clearData.confirm.title"),
                            message = viewModel.getString("settings.clearData.confirm.message"),
                            confirmText = viewModel.getString("button.confirm"),
                            cancelText = viewModel.getString("button.cancel"),
                            onConfirm = {
                                viewModel.onEvent(SettingsUiEvent.ClearData)
                            },
                            onCancel = {}
                        )
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colors.error,
                        contentColor = MaterialTheme.colors.onError
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = viewModel.getString("settings.clearData"), color = MaterialTheme.colors.onError)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScreenshotSaveSection(
    enableLabel: String,
    pathLabel: String,
    enabled: Boolean,
    path: String,
    onEnabledChange: (Boolean) -> Unit,
    onChangePath: () -> Unit
) {
    val strings = PropertiesLocalization.create(Config.STRINGS_NAME)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = enableLabel, color = MaterialTheme.colors.onBackground)
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        }

        if (enabled) {
            TextField(
                path,
                onValueChange = { },
                modifier = Modifier.defaultMinSize(minWidth = 360.dp).padding(end = 10.dp),
                singleLine = true,
                enabled = false,
                label = { Text(pathLabel) },
                trailingIcon = {
                    TooltipArea(tooltip = {
                        Text(strings.get("settings.switch"))
                    }) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            modifier = Modifier.size(24.dp).clickable { onChangePath() }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun WindowSizeSection(
    title: String,
    followLabel: String,
    rememberLabel: String,
    customLabel: String,
    widthLabel: String,
    heightLabel: String,
    applyLabel: String,
    mode: Config.WindowSizeMode,
    customWidth: String,
    customHeight: String,
    onModeChange: (Config.WindowSizeMode) -> Unit,
    onCustomWidthChange: (String) -> Unit,
    onCustomHeightChange: (String) -> Unit,
    onApplyCustom: () -> Unit
) {
    LabeledSection(
        title = title,
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SingleChoiceSegmentedButtonRow {
                val modes = listOf(
                    Config.WindowSizeMode.Follow to followLabel,
                    Config.WindowSizeMode.Remember to rememberLabel,
                    Config.WindowSizeMode.Custom to customLabel
                )
                modes.forEachIndexed { index, (value, label) ->
                    SegmentedButton(
                        selected = mode == value,
                        onClick = { onModeChange(value) },
                        label = {
                            Text(
                                text = label,
                                color = if (mode == value)
                                    MaterialTheme.colors.onPrimary
                                else
                                    MaterialTheme.colors.onSurface
                            )
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = modes.size
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colors.primary,
                            activeContentColor = MaterialTheme.colors.onPrimary,
                            inactiveContainerColor = MaterialTheme.colors.surface,
                            inactiveContentColor = MaterialTheme.colors.onSurface,
                        )
                    )
                }
            }

            if (mode == Config.WindowSizeMode.Custom) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        customWidth,
                        onValueChange = onCustomWidthChange,
                        modifier = Modifier.defaultMinSize(minWidth = 120.dp),
                        singleLine = true,
                        label = { Text(widthLabel) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        customHeight,
                        onValueChange = onCustomHeightChange,
                        modifier = Modifier.defaultMinSize(minWidth = 120.dp),
                        singleLine = true,
                        label = { Text(heightLabel) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    val canApply = customWidth.toIntOrNull()?.let { it > 0 } == true &&
                            customHeight.toIntOrNull()?.let { it > 0 } == true
                    Button(onClick = onApplyCustom, enabled = canApply) {
                        Text(applyLabel)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeSettingsSection(
    title: String,
    themeList: List<Theme>,
    selectedTheme: Theme?,
    onThemeSelected: (Theme) -> Unit
) {
    LabeledSection(
        title = title,
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)
    ) {
        ThemeSelectionGrid(
            themeList = themeList,
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected
        )
    }
}

@Composable
private fun ThemeSelectionGrid(
    themeList: List<Theme>,
    selectedTheme: Theme?,
    onThemeSelected: (Theme) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colors.surface)
            .padding(12.dp)
    ) {
        themeList.forEach { item ->
            ThemeColorButton(
                theme = item,
                isSelected = item == selectedTheme,
                onClick = { onThemeSelected(item) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeColorButton(
    theme: Theme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { Text(theme.label) },
        state = rememberTooltipState()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(theme.color.primary, theme.color.background),
                        startX = 0f,
                        endX = Float.POSITIVE_INFINITY
                    )
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = theme.color.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AdbConfigSection(
    title: String,
    envList: List<Pair<String, Environment>>,
    selectedPath: String,
    customerPath: String,
    onEnvSelected: (Environment) -> Unit,
    onCustomerChange: () -> Unit,
) {
    val strings = PropertiesLocalization.create(Config.STRINGS_NAME)
    LabeledSection(
        title = title,
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)
    ) {
        Column {
            AdbEnvironmentSelector(
                envList = envList,
                selectedPath = selectedPath,
                onEnvSelected = onEnvSelected
            )

            if (selectedPath !in listOf(Environment.System.path, Environment.Program.path)) {
                TextField(
                    customerPath,
                    onValueChange = { },
                    modifier = Modifier.defaultMinSize(minWidth = 360.dp).padding(end = 10.dp, top = 4.dp),
                    enabled = false,
                    trailingIcon = {
                        TooltipArea(tooltip = {
                            Text(strings.get("settings.switch"))
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                null,
                                modifier = Modifier.size(24.dp).clickable {
                                    onCustomerChange()
                                }
                            )
                        }

                    }
                )
            }
        }
    }
}

@Composable
private fun AdbEnvironmentSelector(
    envList: List<Pair<String, Environment>>,
    selectedPath: String,
    onEnvSelected: (Environment) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        envList.forEachIndexed { index, (label, env) ->
            SegmentedButton(
                selected = env.path == selectedPath,
                onClick = { onEnvSelected(env) },
                label = {
                    Text(
                        text = label,
                        color = if (env.path == selectedPath)
                            MaterialTheme.colors.onPrimary
                        else
                            MaterialTheme.colors.onSurface
                    )
                },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = envList.size
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colors.primary,
                    activeContentColor = MaterialTheme.colors.onPrimary,
                    inactiveContainerColor = MaterialTheme.colors.surface,
                    inactiveContentColor = MaterialTheme.colors.onSurface,
                )
            )
        }
    }
}
