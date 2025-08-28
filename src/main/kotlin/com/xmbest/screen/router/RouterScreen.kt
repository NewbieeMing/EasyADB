@file:Suppress("DEPRECATION")

package com.xmbest.screen.router

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RouterScreen(viewModel: RouterViewModule = viewModel()) {
    val uiState = viewModel.uiState.collectAsState().value
    Row(modifier = Modifier.fillMaxSize()) {
        Left(modifier = Modifier.fillMaxHeight().width(240.dp), uiState)
        Right(modifier = Modifier.fillMaxHeight().weight(1f), uiState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Left(
    modifier: Modifier = Modifier,
    uiState: RouterUiState,
    viewModel: RouterViewModule = viewModel()
) {
    Column(
        modifier.background(MaterialTheme.colors.background).padding(start = 12.dp, end = 12.dp)
    ) {
        viewModel.pageList.forEachIndexed { index, item ->
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (index == uiState.index) MaterialTheme.colors.primary
                        else MaterialTheme.colors.background
                    ).clickable {
                        viewModel.onEvent(RouterUiEvent.SelectLeftItem(index))
                    }, icon = {
                    Icon(
                        item.icon,
                        item.icon.name,
                        tint = optionColor(index == uiState.index)
                    )
                }) {
                Text(
                    text = item.name,
                    color = optionColor(index == uiState.index)
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f).padding(bottom = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            ListItem(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        viewModel.onEvent(RouterUiEvent.ShowDeviceList(true))
                    }, icon = {
                    Icon(
                        Icons.Default.Phonelink,
                        contentDescription = "refresh devices",
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier.clickable {
                            viewModel.onEvent(RouterUiEvent.RefreshDevice)
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
                viewModel.onEvent(RouterUiEvent.ShowDeviceList(!uiState.devicesListShow))
            }, modifier = Modifier.width(216.dp)
        ) {
            if (uiState.devices.isEmpty()) {
                DropdownMenuItem(onClick = {
                }) {
                    Text(text = viewModel.getString("device.empty"))
                }
            } else {
                uiState.devices.forEach {
                    DropdownMenuItem(onClick = {
                        viewModel.onEvent(RouterUiEvent.SelectDevice(it))
                    }) {
                        Text(text = it.serialNumber)
                    }
                }
            }
        }
    }
}

@Composable
fun Right(modifier: Modifier, uiState: RouterUiState, viewModel: RouterViewModule = viewModel()) {
    Column(modifier.background(color = MaterialTheme.colors.secondary)) {
        viewModel.pageList[uiState.index].comp()
    }
}

@Composable
fun optionColor(value: Boolean) =
    if (value) MaterialTheme.colors.onPrimary
    else MaterialTheme.colors.onBackground