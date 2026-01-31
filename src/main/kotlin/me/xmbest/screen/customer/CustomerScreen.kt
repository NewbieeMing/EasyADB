package me.xmbest.screen.customer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.xmbest.LocalSnackbarHostState
import me.xmbest.screen.customer.entity.ButtonGroupData
import me.xmbest.screen.customer.entity.InputSendData
import me.xmbest.screen.customer.entity.ShellSendData
import me.xmbest.screen.customer.widget.ButtonGroupWidget
import me.xmbest.screen.customer.widget.InputSendWidget
import me.xmbest.screen.customer.widget.ShellSendWidget

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomerScreen() {
    val viewModel: CustomerViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scroll = rememberScrollState()
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(uiState.toast) {
        if (uiState.toast.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.toast, viewModel.getString("button.confirm"))
            viewModel.onEvent(CustomerUiEvent.UI.Toast(""))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewModel.getString("router.item.quickActions"),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TooltipArea(
                    tooltip = {
                        Surface(
                            color = MaterialTheme.colors.surface,
                            elevation = 4.dp
                        ) {
                            Text(
                                text = viewModel.getString("customer.export.title"),
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                ) {
                    IconButton(
                        onClick = { viewModel.onEvent(CustomerUiEvent.Config.Export) }
                    ) {
                        Icon(
                            Icons.Outlined.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TooltipArea(
                    tooltip = {
                        Surface(
                            color = MaterialTheme.colors.surface,
                            elevation = 4.dp
                        ) {
                            Text(
                                text = viewModel.getString("customer.import.title"),
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                ) {
                    IconButton(
                        onClick = { viewModel.onEvent(CustomerUiEvent.Config.Import) }
                    ) {
                        Icon(
                            Icons.Outlined.Upload,
                            contentDescription = "Import",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TooltipArea(
                    tooltip = {
                        Surface(
                            color = MaterialTheme.colors.surface,
                            elevation = 4.dp
                        ) {
                            Text(
                                text = viewModel.getString("customer.refresh.title"),
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                ) {
                    IconButton(
                        onClick = { viewModel.onEvent(CustomerUiEvent.Config.Refresh) }
                    ) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.configList.forEach { config ->
                when (config) {
                    is InputSendData -> {
                        InputSendWidget(
                            data = config,
                            savedValue = uiState.inputValues[config.uuid] ?: "",
                            onEvent = viewModel::onEvent
                        )
                    }
                    is ButtonGroupData -> {
                        ButtonGroupWidget(
                            data = config,
                            onEvent = viewModel::onEvent
                        )
                    }
                    is ShellSendData -> {
                        ShellSendWidget(
                            data = config,
                            savedValue = uiState.inputValues[config.uuid] ?: "",
                            onEvent = viewModel::onEvent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
