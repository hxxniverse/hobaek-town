package io.github.hxxniverse.hobeaktown.feature.fish

import io.github.hxxniverse.hobeaktown.feature.fish.entity.Fish
import io.github.hxxniverse.hobeaktown.feature.fish.entity.FishingRod
import io.github.hxxniverse.hobeaktown.feature.fish.ui.FishFirstSetUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FishCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("낚시") {
                requires { sender is Player && sender.isOp }
                then("낚싯대") {
                    then("목록") {
                        executes {
                            val rods = FishingRod.getFishingRods()

                            rods.forEachIndexed { index, rod ->
                                player.sendMessage("§9[낚시]§7 [§f번호: $index§7]§7 -§r ${rod.item.itemMeta?.displayName}")
                            }
                        }
                    }
                    then("지급") {
                        then("index" to int()) {
                            executes {
                                val index : Int by it
                                val rods = FishingRod.getFishingRods()

                                if(index < 0 || index >= rods.size) {
                                    player.sendMessage("§9[낚시]§7 낚싯대 목록에서 조회한 낚싯대의 번호를 입력해주세요.")
                                    return@executes
                                }

                                player.inventory.addItem(rods[index].item)
                                player.sendMessage("§9[낚시]§7 입력하신 낚싯대가 성공적으로 지급되었습니다.")
                            }
                        }
                    }
                    then("삭제") {
                        then("index" to int()) {
                            executes {
                                val index: Int by it
                                val rods = FishingRod.getFishingRods()

                                if (index < 0 || index >= rods.size) {
                                    player.sendMessage("§9[낚시]§7 낚싯대 목록에서 조회한 낚싯대의 번호를 입력해주세요.")
                                    return@executes
                                }

                                val rodToRemove = rods[index]
                                FishingRod.removeFishingRod(rodToRemove.item)
                                player.sendMessage("§9[낚시]§7 입력하신 낚싯대가 성공적으로 삭제되었습니다.")
                            }
                        }
                    }
                    then("등급설정") {
                        then("level" to int()) {
                            executes {
                                val level : Int by it

                                if(level !in 1..5) {
                                    player.sendMessage("§9[낚시]§7 1부터 5 사이의 올바른 낚싯대 레벨을 입력하세요.")
                                    return@executes
                                }

                                val item = player.inventory.itemInMainHand
                                if(item.type != Material.FISHING_ROD) {
                                    player.sendMessage("§9[낚시]§7 낚싯대를 들고 낚싯대를 등록해주시기 바랍니다.")
                                    return@executes
                                }

                                if(!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
                                    player.sendMessage("§9[낚시]§7 아무 특징이 없는 낚싯대는 등록할 수 없습니다.")
                                    return@executes
                                }

                                if(FishingRod.getByItemStack(item) != null) {
                                    player.sendMessage("§9[낚시]§7 해당 아이템은 이미 낚싯대로 등록이 되어있습니다.")
                                    return@executes
                                }

                                FishingRod.addFishingRod(item, level)
                                player.sendMessage("§9[낚시]§7 해당 아이템을 성공적으로 $level 레벨로 등록하였습니다.")
                            }
                        }
                    }
                }
                then("물고기") {
                    then("조회") {
                        executes {
                            FishFirstSetUi().open(player)
                        }
                    }
                    then("추가") {
                        then("tier" to int()) {
                            executes {
                                val tier : Int by it

                                if(!(tier in 1..3 || tier == 10)) {
                                    player.sendMessage("§9[낚시]§7 등급을 1~3 또는 레전더리인 경우 10으로 올바르게 입력하세요.")
                                    return@executes
                                }

                                val item = player.inventory.itemInMainHand
                                if(item.type == Material.AIR) {
                                    player.sendMessage("§9[낚시]§7 손에 등록할 아이템(물고기)를 들고 입력해주시기 바랍니다.")
                                    return@executes
                                }

                                if(Fish.getTier(item) != null) {
                                    player.sendMessage("§9[낚시]§7 해당 물고기는 이미 " + Fish.getTier(item) + "등급으로 등록되어 있습니다.")
                                    return@executes
                                }

                                Fish.addFish(item, tier)
                                player.sendMessage("§9[낚시]§7 성공적으로 들고있는 아이템을 $tier 등급으로 등록하였습니다.")
                            }
                        }
                    }
                }
            }
        }
    }
}