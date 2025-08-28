package com.xmbest.screen.file

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xmbest.FILE_SPLIT
import com.xmbest.ddmlib.ClipboardUtil
import com.xmbest.theme.ButtonShape
import com.xmbest.theme.purple

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileHeader(viewModel: FileViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val scrollState = rememberScrollState()
    val rootPath = listOf(Pair(viewModel.getString("file.root"), FILE_SPLIT))
    val pathParts = if (uiState.parentPath == FILE_SPLIT) {
        rootPath
    } else {
        val cleanPath = uiState.parentPath.removePrefix(FILE_SPLIT)
        val parts = cleanPath.split(FILE_SPLIT).filter { it.isNotEmpty() }
        val pathPairs = mutableListOf<Pair<String, String>>()
        var currentPath = ""
        parts.forEachIndexed { index, part ->
            currentPath = if (index == 0) "$FILE_SPLIT$part" else "$currentPath$FILE_SPLIT$part"
            pathPairs.add(Pair(part, currentPath))
        }
        rootPath + pathPairs
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            pathParts.forEachIndexed { index, part ->
                val isLast = index == pathParts.size - 1
                val clickPath = part.second
                if (index > 0) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "",
                        tint = MaterialTheme.colors.onBackground
                    )
                }

                @OptIn(ExperimentalFoundationApi::class)
                ContextMenuArea(
                    items = {
                        listOf(
                            ContextMenuItem(viewModel.getString("file.copyPath")) {
                                ClipboardUtil.setSysClipboardText(clickPath.ifEmpty { FILE_SPLIT })
                            }
                        )
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(ButtonShape)
                            .background(
                                if (isLast) MaterialTheme.colors.primary
                                else purple.copy(0.2f)
                            )
                            .clickable {
                                viewModel.onEvent(FileUiEvent.NavigateToPath(clickPath))
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (index == 0) {
                            Icon(
                                Icons.Default.PhoneAndroid,
                                contentDescription = part.second,
                                tint = if (isLast) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = part.first,
                            color = if (isLast) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // 功能区
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            // 返回按钮
            AnimatedVisibility(
                visible = uiState.parentPath != FILE_SPLIT
            ) {
                FunctionButton(
                    icon = Icons.Default.ArrowBack,
                    text = viewModel.getString("file.back"),
                    onClick = {
                        val parentPath =
                            if (uiState.parentPath.contains(FILE_SPLIT) && uiState.parentPath.lastIndexOf(FILE_SPLIT) > 0) {
                                uiState.parentPath.substringBeforeLast(FILE_SPLIT)
                            } else {
                                FILE_SPLIT
                            }
                        viewModel.onEvent(FileUiEvent.NavigateToPath(parentPath))
                    }
                )
            }

            // 创建文件夹按钮
            FunctionButton(
                icon = Icons.Default.CreateNewFolder,
                text = viewModel.getString("file.newFolder"),
                onClick = {
                    // TODO: 实现创建文件夹功能
                }
            )

            // 创建文件按钮
            FunctionButton(
                icon = Icons.Default.NoteAdd,
                text = viewModel.getString("file.newFile"),
                onClick = {
                    // TODO: 实现创建文件功能
                }
            )

            // 导入文件按钮
            FunctionButton(
                icon = Icons.Default.Upload,
                text = viewModel.getString("file.importFile"),
                onClick = {
                    // TODO: 实现导入文件功能
                }
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