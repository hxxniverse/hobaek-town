package io.github.hxxniverse.hobeaktown.feature.trade

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin

/**
 * "명령어 리스트 ( Command list )
 *
 * /거래신청 (플레이어)
 * 채팅창에 뜨며 승인을 누르면 GUI가 출력됩니다.
 * 거절을 누르면 거래가 취소 되었다는 안내가 뜹니다.
 *
 * GUI 설명 ( GUI Explanation )
 *
 * background - black
 * 2,1 ~ 3,2 - blue glass
 * 7,1 ~ 8,2 - blue glass
 * 1,3 ~ 4,3 - red glass or green glass
 * 6,3 ~ 9,3 - red glass or green glass
 * 1,4 ~ 4,5 - empty
 * 6,4 ~ 9,5 - empty
 * 1,6 ~ 2,6 - red
 * 3,6 ~ 4,6 - green
 * 6,6 ~ 7,6 - red
 * 8,6 ~ 9,6 - green
 *
 * 거래 승인을 누를경우
 * 파란색에는 플레이어 머리
 *
 * 빨간유리 대기중
 * 플레이어가 초록유리를 누르면
 * 해당플레이어 아래 유리는
 * 초록색으로 변경됩니다.
 *
 * 아이템 넣는공간
 *
 * 빨간 - 거절
 * 초록 - 거래 준비완료
 */
class TradeFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        TODO("Not yet implemented")
    }

    override fun onDisable(plugin: JavaPlugin) {
        TODO("Not yet implemented")
    }
}