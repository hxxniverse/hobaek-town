package wasteland

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
data class WasteLandConfigData(
    val sendRewards: Map<Int, @Contextual ItemStack> = mapOf(),
    val graveRewards: Map<Int, @Contextual ItemStack> = mapOf(),
)

object WasteLandConfig : FeatureConfig<WasteLandConfigData>(
    "wasteland.json",
    WasteLandConfigData(),
    WasteLandConfigData.serializer()
)
