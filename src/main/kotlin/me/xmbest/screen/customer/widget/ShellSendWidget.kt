package me.xmbest.screen.customer.widget

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.xmbest.screen.customer.CustomerUiEvent
import me.xmbest.screen.customer.entity.ShellSendData

@Composable
fun ShellSendWidget(
    data: ShellSendData,
    savedValue: String,
    onEvent: (CustomerUiEvent) -> Unit
) {
    var text by remember { mutableStateOf(savedValue) }

    LaunchedEffect(savedValue) {
        text = savedValue
    }

    Column(modifier = Modifier.fillMaxWidth().height(data.minHeight.dp)) {
        Text(
            text = data.title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface
        )
        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 10.dp)) {
            val scroll = rememberScrollState()
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    onEvent(CustomerUiEvent.UI.UpdateInputValue(data.uuid, it))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .scrollable(scroll, Orientation.Vertical)
                    .fillMaxHeight(),
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Button(
                                onClick = {
                                    if (text.isNotBlank()) {
                                        onEvent(CustomerUiEvent.Command.Execute(text))
                                    } else {
                                        onEvent(CustomerUiEvent.UI.Toast(data.hintText))
                                    }
                                },
                                modifier = Modifier.padding(end = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(text = data.btnText)
                            }
                        }
                    }
                },
                placeholder = { Text(data.hintText) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface
                )
            )
        }
    }
}
