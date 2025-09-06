package me.xmbest.screen.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.xmbest.Config
import me.xmbest.LocalDialogState
import me.xmbest.locale.PropertiesLocalization
import me.xmbest.model.Environment
import me.xmbest.model.Theme
import me.xmbest.util.DialogUtil

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState = viewModel.uiState.collectAsState().value
    val dialogState = LocalDialogState.current

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

        LabeledSection(viewModel.getString("settings.other"), modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp)) {
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
            ) {
                Text(text = viewModel.getString("settings.clearData"), color = MaterialTheme.colors.onError)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectionGrid(
    themeList: List<Theme>,
    selectedTheme: Theme?,
    onThemeSelected: (Theme) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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

