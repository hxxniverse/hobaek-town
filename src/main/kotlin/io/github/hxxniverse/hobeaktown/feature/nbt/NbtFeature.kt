package io.github.hxxniverse.hobeaktown.feature.nbt

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class NbtFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        NbtCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class NbtCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        // set display
        // add lore, remove lore, update lore, change type
        plugin.kommand {
            register("more") {
                executes {
                    val amount = 64

                    player.inventory.setItemInMainHand(player.inventory.itemInMainHand.edit {
                        addAmount(
                            amount
                        )
                    })
                    player.sendInfoMessage("아이템의 개수를 추가하였습니다.")
                }
            }
            register("nbt") {
                then("display") {
                    then("set") {
                        then("name" to string(StringType.GREEDY_PHRASE)) {
                            executes {
                                val name: String by it

                                player.inventory.setItemInMainHand(player.inventory.itemInMainHand.edit {
                                    setDisplayName(
                                        name
                                    )
                                })
                                player.sendInfoMessage("아이템의 이름을 변경하였습니다.")
                            }
                        }
                    }
                }
                then("lore") {
                    then("add") {
                        then("text" to string(StringType.GREEDY_PHRASE)) {
                            executes {
                                val text: String by it

                                player.inventory.setItemInMainHand(player.inventory.itemInMainHand.edit { addLore(text) })
                                player.sendInfoMessage("아이템의 설명을 추가하였습니다.")
                            }
                        }
                    }
                    then("remove") {
                        then("index" to int()) {
                            executes {
                                val index: Int by it

                                player.inventory.setItemInMainHand(player.inventory.itemInMainHand.edit {
                                    removeLore(
                                        index
                                    )
                                })
                                player.sendInfoMessage("아이템의 설명을 제거하였습니다.")
                            }
                        }
                    }
                    then("update") {
                        then("index" to int()) {
                            then("text" to string(StringType.GREEDY_PHRASE)) {
                                executes {
                                    val index: Int by it
                                    val text: String by it

                                    player.inventory.setItemInMainHand(player.inventory.itemInMainHand.edit {
                                        setLore(
                                            index,
                                            text
                                        )
                                    })
                                    player.sendInfoMessage("아이템의 설명을 변경하였습니다.")
                                }
                            }
                        }
                    }
                }
                then("type") {
                    then("change") {
                        then("type" to string()) {
                            executes {
                                val type: String by it

                                val material = Material.matchMaterial(type) ?: return@executes

                                player.inventory.setItemInMainHand(player.inventory.itemInMainHand.edit {
                                    setType(
                                        material
                                    )
                                })
                                player.sendInfoMessage("아이템의 종류를 변경하였습니다.")
                            }
                        }
                    }
                }
            }
        }
    }
}