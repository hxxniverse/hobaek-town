package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * 명령어 리스트 ( Command list )
 *
 * /쿠폰 제작 (쿠폰 이름) (사용 기간 (시간기준으로 제작됩니다.))
 * ㄴ 명령어 입력시 gui 창이 열리고 쿠폰에 넣을 아이템을 두고 닫으면 쿠폰이 완성됩니다.
 * /쿠폰 (쿠폰 이름)
 * ㄴ 계정당 1회만 수령 가능합니다.
 * ㄴ 수령 후 재입력시 ["이미 수령하셨습니다"] 출력하기
 */
class CouponFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        CouponCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

