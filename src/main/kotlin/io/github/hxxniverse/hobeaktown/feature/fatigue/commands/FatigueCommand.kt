package io.github.hxxniverse.hobeaktown.feature.fatigue.commands

import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueAreaSelection
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.AreaFatigueConfig
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.FatigueArea
import io.github.hxxniverse.hobeaktown.feature.fatigue.toItemStack
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.maxFatigue
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.emptyLocation
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class FatigueCommand : BaseCommand {
    private var itemIdCounter = 0L;
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("피로도"){
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /피로도 help") }
                then("help") {
                    executes {
                        """
                        |피로도 명령어
                        |  /피로도 리스트: 전체 피로도 구역에 대한 정보를 출력합니다.
                        |  /피로도 구역 [이름]: [이름]의 피로도 구역을 설정합니다.
                        |  /피로도 감소설정 [이름] [분] [피로도 수치]: [이름]의 구역에 [분]마다 [피로도 수치]만큼 피로도를 감소시키도록 설정합니다.
                        |  /피로도 증가설정 [이름] [분] [피로도 수치]: [이름]의 구역에 [분]마다 [피로도 수치]만큼 피로도를 증가시키도록 설정합니다.
                        |  /피로도 기본설정 [분] [피로도 수치]: 피로도 구역 최초 설정 시 자동으로 들어가게 되는 피로도 수치 입니다. (피로도 감소)
                        |  /피로도 아이템감소설정 [분] [피로도 수치]: 들고 있는 아이템이 [분]마다 [피로도 수치]만큼 피로도가 깎이도록 설정합니다.
                        |  /피로도 최대치추가 [추가량]: 명령어를 치는 유저의 피로도 최대치를 [추가량]만큼 증가시킵니다.
                        """.trimIndent().also {
                            text(it).send(player)
                        }
                    }
                }
                then("리스트"){
                    executes {
                        FatigueArea.list(player)
                    }
                }
                then("구역"){
                    executes { player.sendMessage("명령어 도우미: /피로도 help") }
                    then("name" to string(StringType.GREEDY_PHRASE)){
                        executes {
                            val name: String by it

                            val selection = FatigueAreaSelection(
                                name = name,
                                pos1 = emptyLocation(),
                                pos2 = emptyLocation()
                            )
                            player.sendMessage("[ ${name}의 구역을 표시해주세요 ]")
                            player.inventory.addItem(selection.toItemStack())
                        }
                    }
                }
                then("감소설정"){
                    then("name" to string(StringType.QUOTABLE_PHRASE)){
                        then("minute" to int(1, 60)){
                            then("fatigue" to int()){
                                executes {
                                    val name: String by it
                                    val minute: Int by it
                                    val fatigue: Int by it

                                    transaction {
                                        val result = FatigueArea.setAreaFatigue(name, fatigue, minute, true)
                                        if(!result) player.sendMessage("지정한 이름의 구역이 존재하지 않습니다.")
                                        else player.sendMessage("${name} 구역의 피로도 설정이 완료되었습니다.")
                                    }
                                }
                            }
                        }
                    }
                }
                then("증가설정"){
                    then("name" to string(StringType.QUOTABLE_PHRASE)){
                        then("minute" to int(1, 60)){
                            then("fatigue" to int()){
                                executes {
                                    val name: String by it
                                    val minute: Int by it
                                    val fatigue: Int by it

                                    transaction {
                                        val result = FatigueArea.setAreaFatigue(name, fatigue, minute, false)
                                        if(!result) player.sendMessage("지정한 이름의 구역이 존재하지 않습니다.")
                                        else player.sendMessage("$name 구역의 피로도 설정이 완료되었습니다.")
                                    }
                                }
                            }
                        }
                    }
                }
                then("기본설정"){
                    then("minute" to int()){
                        then("fatigue" to int()){
                            executes {
                                val minute: Int by it
                                val fatigue: Int by it

                                if(AreaFatigueConfig.updateConfig(minute, fatigue))
                                    player.sendMessage("모든 구역의 기본 피로도가 ${fatigue} 감소 시간 ${minute}으로 설정되었습니다.")
                                else player.sendMessage("피로도 기본 설정에 실패했습니다. 서버 로그를 확인해주세요.")
                            }
                        }
                    }
                }
                then("아이템감소설정"){
                    // /피로도 아이템감소설정 <분> <깎일 피로도>
                    then("minute" to int()){
                        then("fatigue" to int()){
                            executes {
                                val minute: Int by it
                                val fatigue: Int by it

                                val itemInHand = player.inventory.itemInMainHand
                                if(itemInHand.isEmpty) {
                                    player.sendMessage("손에 아무것도 들고 있지 않습니다.")
                                    return@executes
                                }

                                val itemId = itemIdCounter++;
                                val itemStack: ItemStack = ItemStackBuilder()
                                    .setType(itemInHand.type)
                                    .setDisplayName(text(minute, NamedTextColor.BLUE)
                                        .append(text("분마다 ", NamedTextColor.WHITE))
                                        .append(text(fatigue, NamedTextColor.RED))
                                        .append(text("씩 감소", NamedTextColor.WHITE)))
                                    .addPersistentData("id", itemId)
                                    .addPersistentData("cycle", minute)
                                    .addPersistentData("fatigue", fatigue)
                                    .addPersistentData("unusable", 1.toByte())
                                    .build()
                                player.inventory.setItemInMainHand(itemStack)
                            }
                        }
                    }
                }
                then("최대치추가"){
                    // /피로도 최대치 추가 <추가량>
                    then("num" to int(1, 100)){
                        executes {
                            val num : Int by it

                            if(sender is Player) {
                                val player = sender as Player
                                player.maxFatigue += num
                                player.sendMessage("최대 피로도가 ${player.maxFatigue}이 되었습니다!")
                            }
                        }
                    }
                }
            }
        }
    }
}