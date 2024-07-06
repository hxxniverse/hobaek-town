package io.github.hxxniverse.hobeaktown.feature.fatigue;

import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.component
import kotlinx.serialization.Contextual;
import kotlinx.serialization.Serializable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack

@Serializable
data class FatigueAreaSelection(
        val name: String,
        val pos1: @Contextual Location,
        val pos2: @Contextual Location
)

fun FatigueAreaSelection.toItemStack(): ItemStack {
    return ItemStackBuilder()
            .setType(Material.BLAZE_ROD)
            .setDisplayName(component(name, NamedTextColor.GOLD).append(component(" 구역 지정 막대", NamedTextColor.WHITE)))
            .addLore(component("이 아이템을 [Left]/[Shift + Left]하여 피로도 영역을 선택합니다.", NamedTextColor.GRAY))
            .addLore(component(""))
            .addLore(component("선택된 월드: ", NamedTextColor.GRAY).append(component(pos1.world?.name ?: "알 수 없음", NamedTextColor.WHITE)))
            .addLore(
                component("첫 번째 위치: ", NamedTextColor.GRAY).append(
                    component(
                            "${pos1.blockX}, ${pos1.blockY}, ${pos1.blockZ}",
                            NamedTextColor.WHITE
                    )
                )
            )
            .addLore(
                    component("두 번째 위치: ", NamedTextColor.GRAY).append(
                            component(
                                    "${pos2.blockX}, ${pos2.blockY}, ${pos2.blockZ}",
                                    NamedTextColor.WHITE
                            )
                    )
            )
            .addPersistentData(this)
            .build()
}

fun ItemStack.getItemStackFatigueAreaSelection(): FatigueAreaSelection? {
        return getPersistentData<FatigueAreaSelection>()
}