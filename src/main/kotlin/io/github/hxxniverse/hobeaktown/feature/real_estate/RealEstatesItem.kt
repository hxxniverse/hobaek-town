package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import org.bukkit.Material

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
}