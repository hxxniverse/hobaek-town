package io.github.hxxniverse.hobeaktown.feature.fatigue.commands

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.StringType
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FetigueCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("피로도"){
                requires {
                    sender is Player
                }
                executes {
                    // 명령어 설명
                }
                then("구역 리스트"){

                }
                then("구역"){
                    then("name" to string(StringType.GREEDY_PHRASE)){

                    }
                }
                then("감소설정"){
                    then("args" to string(StringType.GREEDY_PHRASE)){
                        // /피로도 감소설정 <이름> <분> <깎일 피로도>
                    }
                }
                then("증가설정"){
                    then("args" to string(StringType.GREEDY_PHRASE)){
                        // /피로도 증가설정 <이름> <분> <증가할 피로도>
                    }
                }
                then("기본설정"){
                    then("args" to string(StringType.GREEDY_PHRASE)){
                        // /피로도 기본설정 <분> <깎일 피로도>
                    }
                }
                then("아이템감소설정"){
                    then("args" to string(StringType.GREEDY_PHRASE)){
                        // /피로도 아이템감소설정 <분> <깎일 피로도>
                    }
                }
                then("최대치 추가"){
                    then("num" to int()){

                    }
                }
            }
        }
    }
}