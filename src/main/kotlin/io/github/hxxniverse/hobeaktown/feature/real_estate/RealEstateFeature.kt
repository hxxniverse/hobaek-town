package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * GUI 설명 ( GUI Explanation )									비고 ( note )
 * 						"/부동산
 * 본인이 소유하고있는
 * 토지의 GUI가 뜹니다."


"소유한 증서를 클릭하면
 * 빨간색 - 판매
 * 초록색 - 양도
 * 파란색 - 청소
"
 * 						"판매창
 * 빨간색 - 취소
 * 초록색 - 최종 판매
 * "
 * 						"양도창
 * 중간에 어떤 사람에게
 * 양도를 할건지 닉네임을 작성후
 * 빨간색 - 취소
 * 초록색 - 최종 양도"
 *
 * 						"청소창
 * 빨간색 - 취소
 * 파란색 - 청소 실행"
 * 명령어 추가

 *
 */
class RealEstateFeature : BaseFeature {
    override fun enable(plugin: JavaPlugin) {
        RealEstateCommand().register(plugin)
        RealEstateConfig.load()
        Bukkit.getPluginManager().registerEvents(RealEstateListener(), plugin)
    }

    override fun disable(plugin: JavaPlugin) {
    }
}


