package me.xmbest.screen.customer.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import me.xmbest.screen.customer.CustomerUiEvent
import me.xmbest.screen.customer.entity.ButtonGroupData

@Composable
fun ButtonGroupWidget(
    data: ButtonGroupData,
    onEvent: (CustomerUiEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = data.title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface
        )
        StaggeredGrid {
            data.list.forEach { button ->
                Button(
                    onClick = {
                        val commands = button.cmd.split(";")
                        commands.forEach { cmd ->
                            val trimmedCmd = cmd.trim()
                            if (trimmedCmd.isNotEmpty()) {
                                onEvent(CustomerUiEvent.ExecuteCommand(trimmedCmd))
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 10.dp, top = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    )
                ) {
                    Text(
                        text = button.btnText,
                        modifier = Modifier.padding(5.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        var yPosition = 0
        var xPosition = 0
        var maxHeight = 0

        placeables.forEach { placeable ->
            if (xPosition + placeable.width > constraints.maxWidth) {
                xPosition = 0
                yPosition += maxHeight
                maxHeight = 0
            }
            xPosition += placeable.width
            maxHeight = maxOf(maxHeight, placeable.height)
        }

        val totalHeight = yPosition + maxHeight

        // 然后进行布局放置
        layout(constraints.maxWidth, totalHeight) {
            yPosition = 0
            xPosition = 0
            maxHeight = 0

            placeables.forEach { placeable ->
                if (xPosition + placeable.width > constraints.maxWidth) {
                    xPosition = 0
                    yPosition += maxHeight
                    maxHeight = 0
                }

                placeable.placeRelative(x = xPosition, y = yPosition)
                xPosition += placeable.width
                maxHeight = maxOf(maxHeight, placeable.height)
            }
        }
    }
}
