package io.github.hxxniverse.hobeaktown.feature.fatigue

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class FetigueConfigData(
    val fatigue: Int = 100,
    val stauts: Status = Status.Normal,
)

object FetigueConfig : FeatureConfig<FetigueConfigData>(
    "fetigue-config.json",
    FetigueConfigData(),
    FetigueConfigData.serializer()
)