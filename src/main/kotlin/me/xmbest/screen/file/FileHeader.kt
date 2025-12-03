package me.xmbest.screen.file

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.xmbest.FILE_SPLIT
import me.xmbest.LocalDialogState
import me.xmbest.ddmlib.ClipboardUtil
import me.xmbest.theme.ButtonShape
import me.xmbest.util.DialogUtil
import me.xmbest.util.InputDialogUtil

/**
 * 构建路径面包屑导航数据
 */
private fun buildPathParts(parentPath: String, rootLabel: String): List<Pair<String, String>> {
    val rootPath = listOf(Pair(rootLabel, FILE_SPLIT))

    if (parentPath == FILE_SPLIT) {
        return rootPath
    }

    val cleanPath = parentPath.removePrefix(FILE_SPLIT)
    val parts = cleanPath.split(FILE_SPLIT).filter { it.isNotEmpty() }
    val pathPairs = mutableListOf<Pair<String, String>>()

    var currentPath = ""
    parts.forEachIndexed { index, part ->
        currentPath = if (index == 0) "$FILE_SPLIT$part" else "$currentPath$FILE_SPLIT$part"
        pathPairs.add(Pair(part, currentPath))
    }

    return rootPath + pathPairs
}

/**
 * 计算父级路径
 */
private fun getParentPath(currentPath: String): String {
    return if (currentPath.contains(FILE_SPLIT) && currentPath.lastIndexOf(FILE_SPLIT) > 0) {
        currentPath.substringBeforeLast(FILE_SPLIT)
    } else {
        FILE_SPLIT
    }
}

@Composable
fun FileHeader(viewModel: FileViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val pathParts = buildPathParts(uiState.parentPath, viewModel.getString("file.root"))
    val dialogState = LocalDialogState.current

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showCreateFileDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(12.dp)
    ) {
        BreadcrumbNavigation(
            viewModel,
            pathParts = pathParts,
            onNavigate = { path -> viewModel.onEvent(FileUiEvent.NavigateToPath(path)) },
            onCopyPath = { path -> ClipboardUtil.setSysClipboardText(path.ifEmpty { FILE_SPLIT }) },
            copyPathLabel = viewModel.getString("file.copyPath")
        )

        FunctionButtonsRow(
            viewModel = viewModel,
            showBackButton = uiState.parentPath != FILE_SPLIT,
            showDelectFilesButton = uiState.children.isNotEmpty(),
            onBackClick = { viewModel.onEvent(FileUiEvent.NavigateToPath(getParentPath(uiState.parentPath))) },
            onRefreshClick = { viewModel.onEvent(FileUiEvent.Refresh) },
            onNewFolderClick = { showCreateFolderDialog = true },
            onNewFileClick = { showCreateFileDialog = true },
            onImportClick = { viewModel.onEvent(FileUiEvent.Imported) },
            onDeleteAllClick = { showDeleteAllDialog = true },
            backLabel = viewModel.getString("file.back"),
            refreshLabel = viewModel.getString("file.refresh"),
            newFolderLabel = viewModel.getString("file.newFolder"),
            newFileLabel = viewModel.getString("file.newFile"),
            importLabel = viewModel.getString("file.importFile"),
            deleteAllLabel = viewModel.getString("file.deleteAll")
        )
    }

    // 创建文件夹对话框
    if (showCreateFolderDialog) {
        InputDialogUtil.showCreateFolderDialog(
            dialogState = dialogState,
            title = viewModel.getString("file.newFolder"),
            onConfirm = { folderName ->
                viewModel.onEvent(FileUiEvent.CreateFolder(folderName))
                showCreateFolderDialog = false
            },
            onCancel = {
                showCreateFolderDialog = false
            }
        )
    }

    // 创建文件对话框
    if (showCreateFileDialog) {
        InputDialogUtil.showCreateFileDialog(
            dialogState = dialogState,
            title = viewModel.getString("file.newFile"),
            onConfirm = { fileName ->
                viewModel.onEvent(FileUiEvent.CreateFile(fileName))
                showCreateFileDialog = false
            },
            onCancel = {
                showCreateFileDialog = false
            }
        )
    }

    // 删除所有文件确认对话框
    if (showDeleteAllDialog) {
        DialogUtil.showWarning(
            dialogState = dialogState,
            title = viewModel.getString("file.deleteAll"),
            message = viewModel.getString("file.deleteAll.confirm"),
            onConfirm = {
                viewModel.onEvent(FileUiEvent.DeleteAllFiles)
                showDeleteAllDialog = false
            },
            onCancel = { showDeleteAllDialog = false }
        )
    }
}

@Composable
private fun BreadcrumbNavigation(
    viewModel: FileViewModel,
    pathParts: List<Pair<String, String>>,
    onNavigate: (String) -> Unit,
    onCopyPath: (String) -> Unit,
    copyPathLabel: String
) {
    val lazyListState = rememberLazyListState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            LazyRow(
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(pathParts) { index, part ->
                    val isLast = index == pathParts.size - 1
                    val clickPath = part.second

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (index > 0) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "",
                                tint = MaterialTheme.colors.onBackground
                            )
                        }

                        ContextMenuArea(
                            items = {
                                listOf(
                                    ContextMenuItem(copyPathLabel) {
                                        onCopyPath(clickPath)
                                    }
                                )
                            }
                        ) {
                            PathBreadcrumb(
                                text = part.first,
                                isRoot = index == 0,
                                isLast = isLast,
                                onClick = { onNavigate(clickPath) }
                            )
                        }
                    }
                }
            }

            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                adapter = rememberScrollbarAdapter(lazyListState)
            )
        }

        Spacer(Modifier.width(8.dp))
        JumpToPathButton(
            viewModel = viewModel
        )
    }

}

@Composable
private fun PathBreadcrumb(
    text: String,
    isRoot: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(ButtonShape)
            .background(
                if (isLast) MaterialTheme.colors.primary
                else MaterialTheme.colors.surface
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (isRoot) {
            Icon(
                Icons.Default.PhoneAndroid,
                contentDescription = text,
                tint = if (isLast) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = text,
            color = if (isLast) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FunctionButtonsRow(
    viewModel: FileViewModel,
    showBackButton: Boolean,
    showDelectFilesButton: Boolean,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onNewFolderClick: () -> Unit,
    onNewFileClick: () -> Unit,
    onImportClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    backLabel: String,
    refreshLabel: String,
    newFolderLabel: String,
    newFileLabel: String,
    importLabel: String,
    deleteAllLabel: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        AnimatedVisibility(visible = showBackButton) {
            FunctionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                text = backLabel,
                onClick = onBackClick
            )
        }

        FunctionButton(
            icon = Icons.Default.Refresh,
            text = refreshLabel,
            onClick = onRefreshClick
        )

        // 收藏夹按钮
        FavoritesButton(viewModel)

        FunctionButton(
            icon = Icons.Default.CreateNewFolder,
            text = newFolderLabel,
            onClick = onNewFolderClick
        )

        FunctionButton(
            icon = Icons.AutoMirrored.Filled.NoteAdd,
            text = newFileLabel,
            onClick = onNewFileClick
        )

        FunctionButton(
            icon = Icons.Default.FileUpload,
            text = importLabel,
            onClick = onImportClick
        )
        if (showDelectFilesButton) {
            FunctionButton(
                icon = Icons.Default.DeleteSweep,
                text = deleteAllLabel,
                onClick = onDeleteAllClick
            )
        }
    }
}

@Composable
private fun FunctionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(ButtonShape)
            .background(MaterialTheme.colors.surface)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = MaterialTheme.colors.onSurface,
            fontSize = 12.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun JumpToPathButton(
    viewModel: FileViewModel
) {
    TooltipArea(tooltip = { Text(viewModel.getString("file.jumpToClipboard")) }) {
        FunctionButton(
            icon = Icons.Default.ArrowOutward,
            text = viewModel.getString("file.jump"),
            onClick = {
                viewModel.onEvent(FileUiEvent.JumpToClipboardPath)
            }
        )
    }
}

@Composable
private fun FavoritesButton(viewModel: FileViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    var expanded by remember { mutableStateOf(false) }

    Box {
        FunctionButton(
            icon = Icons.Default.Star,
            text = viewModel.getString("favorites.title"),
            onClick = {
                viewModel.onEvent(FileUiEvent.RefreshFavorites)
                expanded = true
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(300.dp)
        ) {
            if (uiState.favorites.isEmpty()) {
                DropdownMenuItem(
                    onClick = { },
                    enabled = false
                ) {
                    Text(
                        text = viewModel.getString("favorites.empty"),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                uiState.favorites.forEach { path ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.onEvent(FileUiEvent.NavigateToFavorite(path))
                            expanded = false
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = path,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(FileUiEvent.ToggleFavorite(path))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = viewModel.getString("favorites.remove"),
                                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}