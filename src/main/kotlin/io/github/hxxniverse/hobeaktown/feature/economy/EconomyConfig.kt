package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class EconomyConfigData(
    val initialMoney: Int = 3000,
    val initialCash: Int = 0,
)

object EconomyConfig : FeatureConfig<EconomyConfigData>(
    "economy-config.json",
    EconomyConfigData(),
    EconomyConfigData.serializer()
)