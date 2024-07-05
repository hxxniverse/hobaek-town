package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object TrafficConfig : FeatureConfig<TrafficConfigData>(
    "traffic",
    TrafficConfigData(
        ItemStack(Material.STONE),
        ItemStack(Material.STONE),
        ItemStack(Material.STONE)
    ),
    TrafficConfigData.serializer()
)

@Serializable
data class TrafficConfigData(
    val busTicket: @Contextual ItemStack,
    val subwayTicket: @Contextual ItemStack,
    val airplaneTicket: @Contextual ItemStack
)