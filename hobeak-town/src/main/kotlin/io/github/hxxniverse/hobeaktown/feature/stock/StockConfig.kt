package io.github.hxxniverse.hobeaktown.feature.stock

import kotlinx.serialization.Serializable

@Serializable
data class StockConfig(
    val fluctuationTime: Int = 60,
    val maxFluctuation: Int = 30,
    val minFluctuation: Int = 30,
)