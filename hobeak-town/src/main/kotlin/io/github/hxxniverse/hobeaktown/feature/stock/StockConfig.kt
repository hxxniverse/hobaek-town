package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class StockConfigData(
    val fluctuationTime: Int = 3600,
)

object StockConfig : FeatureConfig<StockConfigData>(
    "stock-config.json",
    StockConfigData(),
    StockConfigData.serializer(),
)
