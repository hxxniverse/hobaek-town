package io.github.hxxniverse.hobeaktown.feature.school

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Serializable

@Serializable
data class SchoolData(
    val room: String = "",
    val description: String = "",
    var question: String = "",
    var answer: String = ""
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
    fun questionUpdate(question: String, answer: String){
        this.configData.question = question
        this.configData.answer = answer
    }
}