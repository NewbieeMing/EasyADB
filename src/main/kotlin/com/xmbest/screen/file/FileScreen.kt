package com.xmbest.screen.file

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FileScreen(viewModel: FileViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.collectAsState().value
    LaunchedEffect(UInt) {
        viewModel.onEvent(FileUiEvent.Refresh)
    }
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 6.dp)
            .scrollable(scrollState, Orientation.Vertical)
    ) {
        stickyHeader {
            FileHeader(viewModel)
        }
        items(uiState.children) {
            FileContent(it, viewModel)
        }
    }
}