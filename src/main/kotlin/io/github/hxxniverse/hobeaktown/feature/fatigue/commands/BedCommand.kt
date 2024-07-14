package io.github.hxxniverse.hobeaktown.feature.fatigue.commands

import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.Bed
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.Beds
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import io.github.hxxniverse.hobeaktown.util.extension.component

class BedCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("침대") {
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /침대 help") }
                then("help") {
                    executes {
                        """
                        |피로도 침대 명령어
                        |  /침대 생성 [침대 레벨] : Lv x 침대를 생성합니다.
                        |  /침대 설정 [침대 레벨] [주기(초)] [증가 피로도] : 특정 레벨의 침대의 피로도 증가/감소 수치를 변경합니다.
                        """.trimIndent().also {
                            component(it).send(player)
                        }
                    }
                }
                then("생성") {
                    executes { player.sendMessage("명령어 도우미: /침대 help") }
                    then("level" to int()) {
                        executes {
                            val level: Int by it
                            transaction {
                                val bedInfo = Bed.find { Beds.id eq level }.firstOrNull() ?: return@transaction
                                val itemStack: ItemStack = ItemStackBuilder()
                                    .setType(Material.valueOf(bedInfo.color))
                                    .setDisplayName(component("Lv ", getNamedTextColor(bedInfo.color)).append(component(level.toString(), getNamedTextColor(bedInfo.color)).append(
                                        component(" 침대")
                                    )))
                                    .addPersistentData("cycle", bedInfo.cycle)
                                    .addPersistentData("fatigue", bedInfo.fatigue)
                                    .build()
                                player.inventory.addItem(itemStack)
                            }
                        }
                    }
                }
                then("설정") {
                    executes { player.sendMessage("명령어 도우미: /침대 help") }
                    then("level" to int()) {
                        then("cycle" to int()) {
                            then("fatigue" to int()) {
                                executes {
                                    val level: Int by it
                                    val cycle: Int by it
                                    val fatigue: Int by it

                                    transaction {
                                        val bed = Bed.find { Beds.id eq level }.firstOrNull() ?: return@transaction
                                        bed.cycle = cycle
                                        bed.fatigue = fatigue
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getNamedTextColor(color: String): NamedTextColor {
        return when (color) {
            "RED_BED" -> NamedTextColor.RED
            "ORANGE_BED" -> NamedTextColor.GOLD
            "YELLOW_BED" -> NamedTextColor.YELLOW
            "LIME_BED" -> NamedTextColor.GREEN
            "LIGHT_BLUE_BED" -> NamedTextColor.AQUA
            "BLUE_BED" -> NamedTextColor.BLUE
            "PURPLE_BED" -> NamedTextColor.DARK_PURPLE
            else -> NamedTextColor.WHITE // default color if not matched
        }
    }
}