package me.xmbest.screen.customer.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import me.xmbest.screen.customer.CustomerUiEvent
import me.xmbest.screen.customer.entity.InputSendData

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputSendWidget(
    data: InputSendData,
    savedValue: String,
    onEvent: (CustomerUiEvent) -> Unit
) {
    var text by remember { mutableStateOf(savedValue) }

    LaunchedEffect(savedValue) {
        text = savedValue
    }

    val executeCommand = remember(data) {
        {
            if (text.isNotBlank()) {
                val cmd = data.cmd.replace(data.template, text).trim()
                onEvent(CustomerUiEvent.Command.Execute(cmd))
            } else {
                onEvent(CustomerUiEvent.UI.Toast(data.hint))
            }
        }
    }

    val clearText = remember {
        {
            text = ""
            onEvent(CustomerUiEvent.UI.UpdateInputValue(data.uuid, ""))
        }
    }

    val updateText = remember {
        { newText: String ->
            text = newText
            onEvent(CustomerUiEvent.UI.UpdateInputValue(data.uuid, newText))
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = data.title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            TextField(
                value = text,
                onValueChange = updateText,
                trailingIcon = {
                    if (text.isNotBlank()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(20.dp).clickable(onClick = clearText)
                        )
                    }
                },
                singleLine = true,
                placeholder = { Text(data.hint) },
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 5.dp).onKeyEvent {
                    if (it.key.keyCode == Key.Enter.keyCode && it.type == KeyEventType.KeyUp) {
                        executeCommand()
                        true
                    } else {
                        false
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface
                )
            )
            Button(
                onClick = executeCommand,
                modifier = Modifier.width(80.dp).fillMaxHeight().padding(3.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text(text = data.btnText)
            }
        }
    }
}
