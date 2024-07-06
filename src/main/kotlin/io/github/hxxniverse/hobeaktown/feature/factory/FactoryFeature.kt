package io.github.hxxniverse.hobeaktown.feature.factory

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin

/**
 * 기획 ( System Planning )
 * "등급별로 4개가 존재합니다. 가동 시 일정 금액이 빠져나갑니다.
 * 가동 시간이 끝나면 가동 비용의 두배 가량의 돈을 회수 할 수 있습니다.
 * A, B, C, D등급으로 나뉩니다."
 * 플러그인 설명 ( System Explanation )									비고 ( note )
 * "
 * 명령어 리스트 ( Command list )
 * OP만 명령어를 작성 할 수 있습니다.
 *
 * /공장 머신설정
 *
 * /공장 머신제작대 블럭설정"
 *
 * GUI 설명 ( GUI Explanation )									비고 ( note )
 *
 * background black
 * 2,2 4,2 6,2 8,2
 *
 * /공장 머신제작대 블럭설정 설정된 블록을 우클릭하면 공장 블록을 제작 할 수 있는 GUI가 열립니다.
 * 등급안내
 * 좌측부터 A,B,C,sD 입니다"
 *
 *
 *
 *
 *
 *
 *
 *
 * 						"원하는 공장 등급을 누르면
 * 제작대가 열립니다.
 * 재료를 넣고 초록 유리를누르면
 * 인벤토리 안으로 공장블럭이 들어오게 됩니다."
 *
 *
 *
 *
 *
 *
 *
 *
 * 공장 제작 GUI 설명 ( GUI Explanation )									비고 ( note )
 * 						"등급별 공장 블록을 우클릭하면
 * 좌측에는 본인이 우클릭한
 * 공장 블록이 보이며
 * 중앙 3칸은 재료 넣는곳
 * 초록 유리는 제작 시작입니다."
 *
 *
 *
 *
 *
 *
 *
 *
 * 						"제작 시작을 누르면
 * 중앙에 제작까지 얼마나 남았는지 시간을 보여줍니다"
 *
 *
 *
 *
 *
 *
 *
 *
 * 						"시간이 지나면
 * 채팅으로 제작이 완료되었다는
 * 알림을 뜨며
 * 좌측에는 사용한 공장 블록이 표기되며
 * 중앙은 제작 완료된 아이템
 * 초록유리는 얻기 버튼 입니다."
 *
 *
 *
 *
 *
 *
 *
 *
 * 공장 제작 레시피 설정 GUI 설명 ( GUI Explanation )									비고 ( note )
 * 						"/공장 머신설정
 * 순서대로 ABCD 등급입니다.
 * 초록유리 - 레시피 제작
 * 공장블럭 - 블록 레시피 제작
 * 빨간유리 - 받기"
 *
 * 해당 공장블럭 위에있는 초록 유리를 클릭시 나옵니다.
 * 좌측 3칸
 * ㄴ 레시피 필요한 재료 공간
 * 우측 한칸
 * ㄴ 제작후 나올 아이템 공간
 * 초록 유리 - 적용
 * 파란 유리 - 다음페이지"
 *
 * "해당 공장 블록을 누르면
 * 블록 레시피 GUI가 뜹니다.
 * 빈공간 9칸 재료 아이템 공간
 * 초록유리 - 적용"
 */
class FactoryFeature :BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {

    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}