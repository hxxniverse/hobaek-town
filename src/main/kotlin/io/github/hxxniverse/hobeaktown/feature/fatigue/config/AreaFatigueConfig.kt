package io.github.hxxniverse.hobeaktown.feature.fatigue.config

import io.github.hxxniverse.hobeaktown.feature.fatigue.Status
import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class AreaFatigueConfigData(
    val initialCycle: Int = 0,
    val initialFatigue: Int = 0,
)

object AreaFatigueConfig : FeatureConfig<AreaFatigueConfigData>(
    "area-fatigue-config.json",
    AreaFatigueConfigData(),
    AreaFatigueConfigData.serializer()
) {
    fun updateConfig(newCycle: Int, newFatigue: Int): Boolean {
        val newData = AreaFatigueConfigData(newCycle, newFatigue)
        this.configData = newData
        return save()
    }
}