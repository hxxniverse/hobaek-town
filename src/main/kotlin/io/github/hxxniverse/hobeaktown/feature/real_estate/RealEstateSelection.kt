package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.text
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.Serial

// 토지 영역 선택을 위한 정보를 담고 있는 객체
@Serializable
data class RealEstateSelection(
    val name: String,
    val price: Int,
    val due: Int,
    val type: RealEstateType,
    val pos1: @Contextual Location,
    val pos2: @Contextual Location
)

fun RealEstateSelection.toItemStack(): ItemStack {
    return ItemStackBuilder()
        .setType(Material.OAK_SIGN)
        .setDisplayName(text(name, NamedTextColor.GOLD).append(text("의 토지 영역 선택", NamedTextColor.WHITE)))
        .addLore(text("이 아이템을 [Left]/[Shift + Left]하여 토지 영역을 선택합니다.", NamedTextColor.GRAY))
        .addLore(text(""))
        .addLore(text("가격: ", NamedTextColor.GRAY).append(text(price, NamedTextColor.WHITE)))
        .addLore(text("기간: ", NamedTextColor.GRAY).append(text(due, NamedTextColor.WHITE)))
        .addLore(text("타입: ", NamedTextColor.GRAY).append(text(type.name, NamedTextColor.WHITE)))
        .addLore("")
        .addLore(text("선택된 월드: ", NamedTextColor.GRAY).append(text(pos1.world?.name ?: "알 수 없음", NamedTextColor.WHITE)))
        .addLore(
            text("첫 번째 위치: ", NamedTextColor.GRAY).append(
                text(
                    "${pos1.blockX}, ${pos1.blockY}, ${pos1.blockZ}",
                    NamedTextColor.WHITE
                )
            )
        )
        .addLore(
            text("두 번째 위치: ", NamedTextColor.GRAY).append(
                text(
                    "${pos2.blockX}, ${pos2.blockY}, ${pos2.blockZ}",
                    NamedTextColor.WHITE
                )
            )
        )
        .addPersistentData(this)
        .build()
}

fun ItemStack.getItemStackRealEstateSelection(): RealEstateSelection? {
    return getPersistentData<RealEstateSelection>()
}