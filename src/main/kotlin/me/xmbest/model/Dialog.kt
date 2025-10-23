package me.xmbest.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import me.xmbest.Config
import me.xmbest.locale.PropertiesLocalization

enum class DialogType {
    INFO,
    WARNING,
    ERROR,
    SUCCESS,
    CUSTOM
}

private val strings = PropertiesLocalization.create(Config.STRINGS_NAME)

data class DialogState(
    val isVisible: Boolean = false,
    val type: DialogType = DialogType.INFO,
    val title: String = "",
    val message: String = "",
    val confirmText: String = strings.get("button.confirm"),
    val cancelText: String = strings.get("button.cancel"),
    val icon: ImageVector? = null,
    val onConfirm: (() -> Unit)? = null,
    val onCancel: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    val customContent: (@Composable () -> Unit)? = null
)