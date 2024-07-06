package io.github.hxxniverse.hobeaktown.feature.randombox

import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBox
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxChance
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItem
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItems
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class RandomBoxSetItemUi(
    private val randomBox: RandomBox
) : CustomInventory("${randomBox.name} 아이템 설정", 54) {
    init {
        inventory {
            transaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                // chance grade 1,1 ~ 9,1
                button(
                    1 to 1,
                    icon {
                        type = Material.RED_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.RED.chance}%".component()
                    },
                )

                button(
                    1 to 2,
                    icon {
                        type = Material.ORANGE_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.ORANGE.chance}%".component()
                    },
                )

                button(
                    1 to 3,
                    icon {
                        type = Material.YELLOW_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.YELLOW.chance}%".component()
                    },
                )

                button(
                    1 to 4,
                    icon {
                        type = Material.GREEN_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.GREEN.chance}%".component()
                    },
                )

                button(
                    1 to 5,
                    icon {
                        type = Material.BLUE_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.BLUE.chance}%".component()
                    },
                )

                button(
                    1 to 6,
                    icon {
                        type = Material.PURPLE_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.PURPLE.chance}%".component()
                    },
                )

                button(
                    1 to 7,
                    icon {
                        type = Material.BROWN_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.BROWN.chance}%".component()
                    },
                )

                button(
                    1 to 8,
                    icon {
                        type = Material.BLACK_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.BLACK.chance}%".component()
                    },
                )

                button(
                    1 to 9,
                    icon {
                        type = Material.WHITE_STAINED_GLASS_PANE
                        name = "${RandomBoxChance.WHITE.chance}%".component()
                    },
                )

                // 5,6 box item
                button(6 to 5, ItemStack(randomBox.itemStack)) {
                    it.isCancelled = false
                }

                for (i in 1..9) {
                    val chance = RandomBoxChance.entries[i - 1]
                    val items =
                        RandomBoxItem.find { (RandomBoxItems.randomBox eq randomBox.id) and (RandomBoxItems.chance eq chance) }
                            .toList()
                    for (j in 2..5) {
                        if (items.size >= j - 1) {
                            button(j to i, items[j - 2].itemStack) {
                                it.isCancelled = false
                            }
                        } else {
                            empty(j to i)
                        }
                    }
                }

                button(
                    6 to 9,
                    icon {
                        type = Material.GLASS_PANE
                        name = "수정".component()
                    }
                ) {
                    it.isCancelled = true

                    val newRandomBoxItem = getItem(6 to 5)

                    if (newRandomBoxItem == null) {
                        player.sendMessage("아이템을 설정해주세요.".component())
                        return@button
                    }

                    transaction {
                        randomBox.itemStack = newRandomBoxItem.setRandomBox(randomBox)
                    }

                    // remove randombox's  items
                    transaction {
                        RandomBoxItem.find { RandomBoxItems.randomBox eq randomBox.id }.forEach { randomBoxItem ->
                            randomBoxItem.delete()
                        }
                    }

                    // create randombox's items
                    for (i in 1..9) {
                        for (j in 2..5) {
                            val itemStack = getItem(j to i) ?: continue
                            val chance = when (i) {
                                1 -> RandomBoxChance.RED
                                2 -> RandomBoxChance.ORANGE
                                3 -> RandomBoxChance.YELLOW
                                4 -> RandomBoxChance.GREEN
                                5 -> RandomBoxChance.BLUE
                                6 -> RandomBoxChance.PURPLE
                                7 -> RandomBoxChance.BROWN
                                8 -> RandomBoxChance.BLACK
                                9 -> RandomBoxChance.WHITE
                                else -> continue
                            }
                            transaction {
                                RandomBoxItem.new {
                                    this.randomBox = this@RandomBoxSetItemUi.randomBox
                                    this.itemStack = itemStack
                                    this.chance = chance
                                }
                            }
                        }
                    }
                    player.sendMessage("아이템이 수정되었습니다.".component())
                    player.closeInventory()
                }
            }
        }
    }
}