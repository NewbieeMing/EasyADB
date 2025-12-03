package me.xmbest.screen.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.xmbest.LocalDialogState
import me.xmbest.ddmlib.AppInfo
import me.xmbest.ddmlib.ClipboardUtil
import me.xmbest.ddmlib.DeviceOperate
import me.xmbest.ddmlib.Log
import me.xmbest.ddmlib.ProcessInfo
import me.xmbest.ddmlib.loadInfo
import me.xmbest.theme.CardShape
import me.xmbest.theme.blue_primary
import me.xmbest.theme.green_primary
import me.xmbest.theme.yellow_primary
import me.xmbest.util.DialogUtil

@Composable
fun AppScreen(viewModel: AppViewModel = viewModel()) {
    val lazyListState = rememberLazyListState()
    val uiState = viewModel.uiState.collectAsState().value

    DisposableEffect(UInt) {
        viewModel.onEvent(AppUiEvent.Show)
        onDispose {
            viewModel.onEvent(AppUiEvent.Dispose)
        }
    }

    // 监听parentPath变化，自动滚动到顶部
    LaunchedEffect(uiState.filter, uiState.mode) {
        lazyListState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        HeaderTool(viewModel, uiState)

        LazyColumn {
            stickyHeader {
                Header(uiState)
            }
            if (uiState.mode == AppShowMode.ProcessMode) {
                items(uiState.processList) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ProcessItem(it)
                }
            } else {
                items(uiState.appList) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppItem(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(appInfo: AppInfo, viewModel: AppViewModel = viewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // 使用状态来跟踪版本信息
    var versionName by remember { mutableStateOf(appInfo.versionName) }
    var versionCode by remember { mutableStateOf(appInfo.versionCode) }
    var size by remember { mutableStateOf(appInfo.size) }
    var minSdk by remember { mutableStateOf(appInfo.minSdk) }
    var targetSdk by remember { mutableStateOf(appInfo.targetSdk) }
    var isLoading by remember { mutableStateOf(false) }

    // 启动时加载版本信息
    LaunchedEffect(appInfo.packageName) {
        isLoading = true
        appInfo.loadInfo()
        versionName = appInfo.versionName
        versionCode = appInfo.versionCode
        size = appInfo.size
        minSdk = appInfo.minSdk
        targetSdk = appInfo.targetSdk
        isLoading = false
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .height(64.dp)
            .clip(CardShape)
            .background(MaterialTheme.colors.surface)
            .hoverable(interactionSource)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SelectionContainer(Modifier.weight(3.5f)) {
            Text(
                text = appInfo.packageName,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        SelectionContainer(Modifier.weight(2f)) {
            Text(
                text = if (isLoading) viewModel.getString("app.loading") else versionName,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        SelectionContainer(Modifier.weight(2f)) {
            Text(
                text = if (isLoading) viewModel.getString("app.loading") else versionCode,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        SelectionContainer(Modifier.weight(2f)) {
            Text(
                text = if (isLoading) viewModel.getString("app.loading") else "$minSdk/$targetSdk",
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        SelectionContainer(Modifier.weight(2f)) {
            Text(
                text = if (isLoading) viewModel.getString("app.loading") else size,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        val dialogState = LocalDialogState.current
        Row(Modifier.weight(3.5f), horizontalArrangement = Arrangement.SpaceBetween) {
            if (isHovered) {
                IconButton(onClick = {
                    ClipboardUtil.setSysClipboardText(appInfo.path)
                }) {
                    TooltipArea({ Text(viewModel.getString("file.copyPath")) }) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = blue_primary
                        )
                    }
                }
                IconButton(onClick = {
                    viewModel.onEvent(AppUiEvent.StartApp(appInfo.packageName))
                }) {
                    TooltipArea({ Text(viewModel.getString("app.startApp")) }) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = "",
                            modifier = Modifier.size(28.dp),
                            tint = green_primary
                        )
                    }
                }
                IconButton(onClick = {
                    DialogUtil.showWarning(
                        dialogState = dialogState,
                        message = viewModel.getString("app.clearData.confirm")
                            .format(appInfo.packageName),
                        onConfirm = {
                            viewModel.onEvent(AppUiEvent.ClearData(appInfo.packageName))
                        },
                        onCancel = {}
                    )
                }) {
                    TooltipArea({ Text(viewModel.getString("settings.clearData")) }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = yellow_primary
                        )
                    }
                }
                IconButton(onClick = {
                    DialogUtil.showWarning(
                        dialogState = dialogState,
                        message = viewModel.getString("app.uninstall.confirm")
                            .format(appInfo.packageName),
                        onConfirm = {
                            viewModel.onEvent(AppUiEvent.Uninstall(appInfo.packageName))
                        },
                        onCancel = {}
                    )
                }) {
                    TooltipArea({ Text(viewModel.getString("app.uninstall")) }) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Header(uiState: AppUiState, viewModel: AppViewModel = viewModel()) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).clip(CardShape)
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (uiState.mode == AppShowMode.ProcessMode) {
            DeviceOperate.topHeadColumns.forEach { item ->
                Text(
                    text = item,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                )
            }
            Text(
                "name",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(5f).align(Alignment.CenterVertically)
            )
            Row(Modifier.weight(1.5f)) {
                Text(
                    "action",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        } else {
            Text(
                text = "packageName",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(3.5f).padding(start = 4.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "versionName",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f).align(Alignment.CenterVertically)
            )
            Text(
                text = "versionCode",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f).align(Alignment.CenterVertically)
            )
            Text(
                text = "min/target",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f).align(Alignment.CenterVertically)
            )
            Text(
                text = "size",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f).align(Alignment.CenterVertically)
            )
            Text(
                text = viewModel.getString("app.action"),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(3.5f).align(Alignment.CenterVertically)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProcessItem(process: ProcessInfo, viewModel: AppViewModel = viewModel()) {
    val list = listOf(
        process.pid,
        process.user,
        process.cpu,
        process.time,
        process.virt,
        process.res,
        process.shr,
        process.mem
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(64.dp)
            .clip(CardShape)
            .background(MaterialTheme.colors.surface)
            .hoverable(interactionSource)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        list.forEach { item ->
            SelectionContainer(Modifier.weight(1f)) {
                Text(
                    text = item,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        SelectionContainer(Modifier.weight(5f)) {
            Text(
                process.name,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Row(Modifier.weight(1.5f), horizontalArrangement = Arrangement.SpaceBetween) {
            if (isHovered) {
                IconButton(onClick = {
                    viewModel.onEvent(AppUiEvent.Kill(listOf(process.pid)))
                }) {
                    TooltipArea({ Text(viewModel.getString("app.kill")) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
                IconButton(onClick = {
                    viewModel.onEvent(AppUiEvent.ForceStop(process.name))
                }) {
                    TooltipArea({ Text(viewModel.getString("app.forceStop")) }) {
                        Icon(
                            imageVector = Icons.Outlined.Stop,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderTool(viewModel: AppViewModel, uiState: AppUiState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        SearchBarDefaults.InputField(
            query = uiState.filter,
            onQueryChange = {
                Log.d("", "onQueryChange $it")
                viewModel.onEvent(AppUiEvent.ChangeFilter(it))
            },
            onSearch = {
                Log.d("", "onSearch $it")
                viewModel.onEvent(AppUiEvent.ChangeFilter(it))
            },
            expanded = false,
            onExpandedChange = { },
            placeholder = { Text(viewModel.getString("app.search")) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (uiState.filter.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.onEvent(AppUiEvent.ChangeFilter("")) },
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                            .background(MaterialTheme.colors.surface)
                    ) {
                        Icon(Icons.Default.Cancel, "")
                    }
                }
            },
            modifier = Modifier.weight(1f).clip(CircleShape)
                .background(MaterialTheme.colors.surface)
        )

        uiState.buttonList.filter { it.isShow() }.forEach { button ->
            Spacer(modifier = Modifier.width(16.dp))
            AppIconButton(
                icon = button.icon,
                description = button.description,
                backgroundColor = if (button.isSelected()) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                foregroundColor = if (button.isSelected()) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                click = button.onClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconButton(
    icon: ImageVector,
    description: String,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color = MaterialTheme.colors.onSurface,
    click: () -> Unit,
) {
    TooltipArea({ Text(description) }) {
        IconButton(
            onClick = {
                click()
            }, modifier = Modifier.size(48.dp).clip(CircleShape).background(backgroundColor)
        ) {
            Icon(icon, "", tint = foregroundColor)
        }
    }

}
