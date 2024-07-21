package io.github.hxxniverse.hobeaktown.feature.school

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class SchoolData(
    val room: String = "",
    val description: String = ""
)

object SchoolDataConfig: FeatureConfig<SchoolData>(
    "lecture-config.json",
    SchoolData(),
    SchoolData.serializer()
) {
    fun updateConfig(room: String, description: String){
        val newData = SchoolData(room, description)
        this.configData = newData
    }
}