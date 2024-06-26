package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.pretty
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object RealEstatesItem {
    // 토지 감정서
    val LAND_APPRAISAL_CERTIFICATE = ItemStackBuilder()
        .setType(Material.PAPER)
        .setDisplayName("토지 감정서")
        .setLore(
            "토지 감정서를 사용하면 토지의 등급을 알 수 있습니다.",
            "토지의 등급은 C, B, A, S, R 등급이며 R등급이 가장 높습니다.",
            "토지의 등급이 정해지지 않은 자신의 토지 안에서 사용해주세요.",
        )
        .build()

    // 부동산 증서
    val REAL_ESTATE_CERTIFICATE: (RealEstate) -> ItemStack = { realEstate ->
        ItemStackBuilder()
            .setType(Material.PAPER)
            .setDisplayName(realEstate.name + "의 부동산 증서")
            .setLore(
                "위치: ${realEstate.centerLocation.pretty()}",
                "",
                "해당 증서는 재발급되지 않습니다.",
                "분실하지 않도록 주의 부탁드립니다.",
                "우클릭 시 해당 부동산의 정보를 확인할 수 있습니다."
            )
            .addPersistentData("realEstateId", realEstate.id.value)
            .build()
    }
}