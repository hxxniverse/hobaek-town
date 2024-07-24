package io.github.hxxniverse.hobeaktown.feature.wasteland

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Brush
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import io.github.hxxniverse.hobeaktown.feature.wasteland.ui.WastelandSetupUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WastelandCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("황무지") {
                requires { sender is Player && sender.isOp }
                then("아이템설정") {
                    then("name" to string()) {
                        executes {
                            val name : String by it

                            if(WastelandSetup.getSetupByCode(name) != null) {
                                player.sendMessage("§6[황무지]§7 해당 이름으로 등록된 황무지들이 이미 존재합니다.")
                            }

                            WastelandSetupUi(name).open(player)
                        }
                    }
                }
                then("솔") {
                    then("목록") {
                        executes {
                            val brushes: List<Brush> = Brush.getRegisteredBrushes()

                            if(brushes.isEmpty()) {
                                player.sendMessage("§6[황무지]§7 아직 아무 솔도 등록되어 있지 않습니다.")
                                return@executes
                            }

                            brushes.forEach {
                                player.sendMessage("§6[이름:§7 " + it.name + "§6] [레벨:§7 " + it.level + "§6]§e -§r " + it.item.itemMeta.displayName)
                            }
                        }
                    }
                    then("지급") {
                        then("name" to string()) {
                            executes {
                                val name : String by it
                                val brush = Brush.getByName(name)

                                if(brush == null) {
                                    player.sendMessage("§6[황무지]§7 $name 이라는 이름을 가진 등록된 솔이 존재하지 않습니다.")
                                    return@executes
                                }

                                player.inventory.addItem(brush.item)
                                player.sendMessage("§6[황무지]§7 $name (으)로 등록된 솔을 지급하였습니다.")
                            }
                        }
                    }
                    then("등록") {
                        then("name" to string()) {
                            then("level" to int()) {
                                executes {
                                    val name : String by it
                                    val level : Int by it

                                    if(Brush.getByName(name) != null) {
                                        player.sendMessage("§6[황무지]§7 해당 이름의 솔은 이미 등록되어 있습니다.")
                                        return@executes
                                    }

                                    val item = player.inventory.itemInMainHand

                                    if(item.type != Material.BRUSH) {
                                        player.sendMessage("§6[황무지]§7 솔 아이템을 들고 등록해주시기 바랍니다.")
                                        return@executes
                                    }

                                    val itemmeta = item.itemMeta

                                    if(itemmeta == null || !itemmeta.hasDisplayName()) {
                                        player.sendMessage("§6[황무지]§7 아무 특징이 없는 솔 아이템은 등록할 수 없습니다.")
                                        return@executes
                                    }

                                    if(Brush.getByItemStack(item) != null) {
                                        player.sendMessage("§6[황무지]§7 해당 아이템은 이미 ${Brush.getByItemStack(item)!!.name} (으)로 등록되어 있습니다.")
                                        return@executes
                                    }

                                    Brush.registerBrush(name, item, level)
                                    player.sendMessage("§6[황무지]§7 들고있는 솔을 $level 레벨의 $name (으)로 등록하였습니다.")
                                }
                            }
                        }
                    }
                    then("삭제") {
                        then("name" to string()) {
                            executes {
                                val name : String by it

                                if(Brush.getByName(name) == null) {
                                    player.sendMessage("§6[황무지]§7 해당 이름의 솔은 이미 존재하지 않습니다.")
                                    return@executes
                                }

                                Brush.unregisterBrush(name)
                                player.sendMessage("§6[황무지]§7 입력하신 $name 솔을 삭제하였습니다.")
                            }
                        }
                    }
                }
            }
        }
    }
}