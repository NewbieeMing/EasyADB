package me.xmbest.screen.customer.entity

data class ButtonGroupData(
    val title: String,
    val list: List<ButtonData>
) : BaseFastBroadData(FastBroadType.BUTTON_GROUP)
