@file:Suppress("DEPRECATION")

package me.xmbest.screen.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.xmbest.LocalSnackbarHostState
import me.xmbest.LocalDialogState
import me.xmbest.component.GlobalDialog
import me.xmbest.model.DialogState


@Composable
fun NaviScreen(viewModel: NaviViewModule = viewModel()) {
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val dialogState = remember { mutableStateOf(DialogState()) }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
        LocalDialogState provides dialogState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Left(modifier = Modifier.fillMaxHeight().width(240.dp), uiState)
                Right(modifier = Modifier.fillMaxHeight().weight(1f), uiState)
            }
        }

        // 添加全局弹窗
        GlobalDialog()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Left(
    modifier: Modifier = Modifier, uiState: NaviUiState, viewModel: NaviViewModule = viewModel()
) {
    Column(
        modifier.background(MaterialTheme.colors.background).padding(start = 12.dp, end = 12.dp)
    ) {
        viewModel.pageList.forEachIndexed { index, item ->
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                modifier = Modifier.height(44.dp).clip(RoundedCornerShape(8.dp)).background(
                    if (index == uiState.index) MaterialTheme.colors.primary
                    else MaterialTheme.colors.background
                ).clickable {
                    viewModel.onEvent(NaviUiEvent.Navigation.SelectLeftItem(index))
                }, icon = {
                    Icon(
                        item.icon, item.icon.name, tint = optionColor(index == uiState.index)
                    )
                }) {
                Text(
                    text = item.name, color = optionColor(index == uiState.index)
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f).padding(bottom = 8.dp).fillMaxWidth(), verticalAlignment = Alignment.Bottom
        ) {
            ListItem(modifier = Modifier.height(44.dp).clip(RoundedCornerShape(8.dp)).clickable {
                viewModel.onEvent(NaviUiEvent.DeviceManagement.ShowDeviceList(true))
            }, icon = {
                Icon(
                    Icons.Default.Phonelink,
                    contentDescription = "refresh devices",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.clickable {
                        viewModel.onEvent(NaviUiEvent.DeviceManagement.RefreshDevice)
                    })
            }) {
                Text(
                    uiState.device?.serialNumber ?: viewModel.getString("device.select"),
                    maxLines = 2,
                    color = MaterialTheme.colors.onBackground,
                )
            }
        }

        DropdownMenu(
            expanded = uiState.devicesListShow, onDismissRequest = {
                viewModel.onEvent(NaviUiEvent.DeviceManagement.ShowDeviceList(!uiState.devicesListShow))
            }, modifier = Modifier.width(216.dp)
        ) {
            if (uiState.devices.isEmpty()) {
                DropdownMenuItem(onClick = {}) {
                    Text(text = viewModel.getString("device.empty"))
                }
            } else {
                uiState.devices.forEach {
                    DropdownMenuItem(onClick = {
                        viewModel.onEvent(NaviUiEvent.DeviceManagement.SelectDevice(it))
                    }) {
                        Text(text = it.serialNumber)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Right(modifier: Modifier, uiState: NaviUiState, viewModel: NaviViewModule = viewModel()) {
    Column(modifier.background(color = MaterialTheme.colors.secondary)) {
        AnimatedContent(
            targetState = uiState.index,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 250,
                        delayMillis = 50
                    )
                ) with slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseInCubic
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 200
                    )
                )
            }
        ) { targetIndex ->
            viewModel.pageList[targetIndex].comp()
        }
    }
}

@Composable
fun optionColor(value: Boolean) = if (value) MaterialTheme.colors.onPrimary
else MaterialTheme.colors.onBackground