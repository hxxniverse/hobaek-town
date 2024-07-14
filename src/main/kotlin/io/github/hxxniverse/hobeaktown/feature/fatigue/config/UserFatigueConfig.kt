package io.github.hxxniverse.hobeaktown.feature.fatigue.config

import io.github.hxxniverse.hobeaktown.feature.fatigue.Status
import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class UserFatigueConfigData(
    val initialFatigue: Int = 100,
    val initialMaxFatigue: Int = 100,
    val initialStatus: Status = Status.Normal,
)

object UserFatigueConfig : FeatureConfig<UserFatigueConfigData>(
    "user-fatigue-config.json",
    UserFatigueConfigData(),
    UserFatigueConfigData.serializer()
)