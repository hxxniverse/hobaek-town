package io.github.hxxniverse.hobeaktown.feature.randombox

import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBox
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxChance
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItem
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItems
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class RandomBoxItemListUi(
    private val randomBox: RandomBox
) : CustomInventory("${randomBox.name} 아이템 목록", 54) {
    init {
        inventory {
            transaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                button(
                    1 to 1,
                    ItemStack(Material.RED_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.RED.chance}%") },
                )
                button(
                    1 to 2,
                    ItemStack(Material.ORANGE_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.ORANGE.chance}%") },
                )

                button(
                    1 to 3,
                    ItemStack(Material.YELLOW_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.YELLOW.chance}%") },
                )

                button(
                    1 to 4,
                    ItemStack(Material.GREEN_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.GREEN.chance}%") },
                )

                button(
                    1 to 5,
                    ItemStack(Material.BLUE_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.BLUE.chance}%") },
                )

                button(
                    1 to 6,
                    ItemStack(Material.PURPLE_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.PURPLE.chance}%") },
                )

                button(
                    1 to 7,
                    ItemStack(Material.BROWN_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.BROWN.chance}%") },
                )

                button(
                    1 to 8,
                    ItemStack(Material.BLACK_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.BLACK.chance}%") },
                )

                button(
                    1 to 9,
                    ItemStack(Material.WHITE_STAINED_GLASS_PANE).edit { setDisplayName("확률: ${RandomBoxChance.WHITE.chance}%") },
                )

                for (i in 1..9) {
                    val chance = RandomBoxChance.entries[i - 1]
                    val items =
                        RandomBoxItem.find { (RandomBoxItems.randomBox eq randomBox.id) and (RandomBoxItems.chance eq chance) }
                            .toList()
                    for (j in 2..5) {
                        if (items.size >= j - 1) {
                            button(j to i, items[j - 2].itemStack) {
                                it.isCancelled = true
                            }
                        } else {
                            empty(j to i)
                        }
                    }
                }
            }
        }
        onPlayerInventoryClick {
            it.isCancelled = true
        }
    }
}