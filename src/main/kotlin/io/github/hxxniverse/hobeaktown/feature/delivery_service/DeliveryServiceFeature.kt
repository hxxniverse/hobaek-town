package io.github.hxxniverse.hobeaktown.feature.delivery_service

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBoxItems
import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBoxes
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

/**
 * GUI 설명 ( GUI Explanation )
 * 						"/택배함
 *
 * 빈공간에는 본인이 얻은 아이템들이 들어가 있으며
 * 좌클릭 또는 우클릭시
 * 인벤토리로 받을 수 있습니다
 *
 * 초록 유리 다음페이지
 * 2번째 창부턴 이전페이지도 있어야합니다."
 * 						"택배상자를 들고
 * 쉬프트 좌클릭시
 * 아이템 목록들을 볼 수 있습니다."
 * 						"/택배 제작 (이름)
 *
 * 맨아래 빈공간에는
 * 아이템에더로 제작된 박스가 들어갑니다.
 * 초록 유리클릭시 설정이 완료되며 인벤토리로 들어옵니다
 *
 * 파란 유리 적용 입니다."
 */
class DeliveryServiceFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(DeliveryBoxes, DeliveryBoxItems)
        }

        DeliveryServiceCommand().register(plugin)
        Bukkit.getPluginManager().registerEvents(DeliveryServiceListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}