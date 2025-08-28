package com.xmbest.screen.file

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ddmlib.FileListingService
import com.xmbest.theme.CardShape
import com.xmbest.theme.ChipShape
import com.xmbest.theme.TextFieldShape
import com.xmbest.theme.purple

@Composable
fun FileContent(file: FileListingService.FileEntry, viewModel: FileViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 6.dp, top = 6.dp)
            .clip(CardShape).background(MaterialTheme.colors.surface)
            .combinedClickable(onDoubleClick = {
                if (file.isDirectory) {
                    viewModel.onEvent(
                        FileUiEvent.NavigateToPath(
                            viewModel.calculatePath(
                                uiState.parentPath,
                                file.name
                            )
                        )
                    )
                }
            }, onClick = {}).padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        val fileTypeInfo = viewModel.getFileTypeInfo(file.type)
        Icon(
            imageVector = fileTypeInfo.icon,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.clip(ChipShape).background(MaterialTheme.colors.primary)
                .padding(8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = file.name,
                    color = MaterialTheme.colors.onSurface,
                    style = TextStyle.Default.copy(fontSize = 18.sp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = viewModel.byte2Gb(file.size),
                    color = MaterialTheme.colors.onSurface.copy(0.6f),
                    style = TextStyle.Default.copy(fontSize = 12.sp),
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fileTypeInfo.text,
                    color = MaterialTheme.colors.primary,
                    style = TextStyle.Default.copy(fontSize = 14.sp),
                    modifier = Modifier.clip(TextFieldShape)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = file.permissions,
                    color = purple,
                    style = TextStyle.Default.copy(fontSize = 14.sp),
                    modifier = Modifier.clip(TextFieldShape).background(purple.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = file.date + " " + file.time,
                    color = MaterialTheme.colors.onBackground,
                    style = TextStyle.Default.copy(fontSize = 14.sp)
                )
            }
        }
    }
}