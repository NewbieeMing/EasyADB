package me.xmbest.screen.empty

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhonelinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.xmbest.screen.navigation.NaviUiEvent
import me.xmbest.screen.navigation.NaviViewModule

@Composable
fun EmptyScreen(viewModel: NaviViewModule = viewModel()) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (MaterialTheme.colors.secondary == MaterialTheme.colors.background) {
                    MaterialTheme.colors.surface
                } else MaterialTheme.colors.secondary
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 设备未连接图标
            Icon(
                imageVector = Icons.Default.PhonelinkOff,
                contentDescription = "No device connected",
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(120.dp)
            )

            // 主标题
            Text(
                text = viewModel.getString("empty.device.title"),
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )

            // 副标题说明
            Text(
                text = viewModel.getString("empty.device.description"),
                style = MaterialTheme.typography.body1.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // 刷新按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary)
                    .clickable {
                        viewModel.onEvent(NaviUiEvent.DeviceManagement.RefreshDevice)
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = viewModel.getString("empty.device.refresh"),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.button.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // 帮助提示
            Text(
                text = viewModel.getString("empty.device.tip"),
                style = MaterialTheme.typography.caption.copy(
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}