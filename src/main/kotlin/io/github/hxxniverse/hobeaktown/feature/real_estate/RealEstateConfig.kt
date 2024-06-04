package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable


object RealEstateConfig : FeatureConfig<RealEstateConfigData>(
    "real_estate.json",
    RealEstateConfigData(),
    RealEstateConfigData.serializer()
)

@Serializable
data class RealEstateConfigData(
    val realEstateWorld: List<String> = listOf("world"),
)