package wasteland

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

@Serializable
data class WasteLandConfigData(
    val locIdMap: Map<@Contextual Location, String> = mapOf(),
    val idRewardMap: Map<String, Map<Int, @Contextual ItemStack>> = mapOf(),
    val brushLevelMap: Map<@Contextual ItemStack, Int> = mapOf()
)

object WasteLandConfig : FeatureConfig<WasteLandConfigData>(
    "wasteland.json",
    WasteLandConfigData(),
    WasteLandConfigData.serializer()
)
