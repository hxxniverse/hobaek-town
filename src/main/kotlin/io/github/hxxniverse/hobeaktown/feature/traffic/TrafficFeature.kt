package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils

/**
 * 	기획 ( System Planning )
 * 	"버스 티켓, 지하철, 비행기 티켓을 구매해야 모델링에 올라갈 수 있습니다.
 * 버스, 지하철에 탑승시 10초뒤에 [""다음역""으로 출발합니다.] 라는 타이틀이 뜨고
 * 버스 음성 / 지하철 음성 출력하면서 가는 길의 풍경이 보입니다.
 * 10초간 고정된 풍경을 보여주고 다음 역으로 도착합니다.
 * 비행기는 10분뒤 도착합니다
 * 만약 내리면 다시 티켓을 구매해야 합니다. "
 *
 * 명령어 리스트 ( Command list )
 *
 * op 명령어
 *
 * /버스역 지정 (역 이름) - pos1 pos2로 정할 수 있습니다.
 * /버스역 풍경 (역 이름) - 해당 역에서 보여줄 풍경을 기록할 수 있습니다.
 * /버스역 순서 (번호) (역 이름) - 해당 역의 순서를 지정합니다.
 *
 * /지하철역 지정 (역 이름) - pos1 pos2로 정할 수 있습니다.
 * /지하철역 풍경 (역 이름) - 해당 역에서 보여줄 풍경을 기록할 수 있습니다.
 * /지하철역 순서 (번호) (역 이름) - 해당 역의 순서를 지정합니다.
 *
 * /비행기 지정 출발 (공항이름) - pos1 pos2로 정할 수 있습니다.
 * /비행기 지정 복귀 (공항이름) - pos1 pos2로 정할 수 있습니다.
 * /비행기 풍경 (역 이름) - 해당 역에서 보여줄 풍경을 기록할 수 있습니다.
 * 비행기 출발에서 탑승하면 복귀 지정중 1곳으로 랜덤으로 이동 됩니다.
 * 비행기 복귀에서 탑승하면 출방 지점으로 이동됩니다.
 *
 * /버스 티켓 생성 (개수) - op 명령어입니다.
 * /지하철 티켓 생성 (개수) - op명령어입니다.
 * /비행기 티켓 생성 (개수) - op명령어입니다.
 *
 * /버스 티켓 아이템설정 - 아이템을 들고 명령어 입력시 티켓 아이템으로 설정됩니다.
 * /지하철 티켓 아이템설정 - 아이템을 들고 명령어 입력시 티켓 아이템으로 설정됩니다.
 * /비행기 티켓 아이템설정 - 아이템을 들고 명령어 입력시 티켓 아이템으로 설정됩니다."
 */
class TrafficFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
             SchemaUtils.create(BusStations)
             SchemaUtils.create(SubwayStations)
             SchemaUtils.create(Airplanes)
        }
        TrafficCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}