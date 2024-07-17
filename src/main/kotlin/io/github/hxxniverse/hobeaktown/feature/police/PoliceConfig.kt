package io.github.hxxniverse.hobeaktown.feature.police

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import io.github.hxxniverse.hobeaktown.util.edit
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
data class PoliceConfigData(
    val threeStepStickItem: @Contextual ItemStack,
    val handcuffsItem: @Contextual ItemStack,
    val detentionCenterLocation: @Contextual Location,
)

object PoliceConfig : FeatureConfig<PoliceConfigData>(
    "police.json",
    PoliceConfigData(
        ItemStack(Material.STICK).edit { setDisplayName("삼단봉") },
        ItemStack(Material.IRON_BARS).edit { setDisplayName("수갑") },
        Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0, 0.0F, 0.0F)
    ),
    PoliceConfigData.serializer()
)