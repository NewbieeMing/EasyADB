package me.xmbest.screen.customer

import me.xmbest.screen.customer.entity.BaseFastBroadData

data class CustomerUiState(
    val configList: List<BaseFastBroadData> = emptyList(),
    val toast: String = "",
    val inputValues: Map<String, String> = emptyMap() // uuid -> value mapping for persistent input
)
