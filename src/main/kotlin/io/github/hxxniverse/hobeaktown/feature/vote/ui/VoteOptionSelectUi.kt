package io.github.hxxniverse.hobeaktown.feature.vote.ui

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class VoteOptionSelectUi(
    private val vote: Vote,
) : CustomInventory("VoteOptionSelect", 54) {

    private var selected: Int = -1

    init {
        inventory {
            loggedTransaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                button(
                    itemStack = icon { type = Material.PAPER; name = vote.question.component() },
                    from = 1 to 4, to = 1 to 6
                )

                val optionSlots = listOf(
                    2 to 2,
                    2 to 4,
                    2 to 6,
                    2 to 8,
                    4 to 2,
                    4 to 4,
                    4 to 6,
                    4 to 8
                )

                vote.options.split(",").forEachIndexed { index, option ->
                    button(
                        optionSlots[index],
                        itemStack = ItemStackBuilder()
                            .setType(Material.PAPER)
                            .setDisplayName(option.component())
                            .setLore(if (index == selected) listOf("§a선택됨") else listOf("§7선택하지 않음"))
                            .addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                            .build()
                            .let { if (index == selected) it.edit { setAmount(1).addUnsafeEnchantment(Enchantment.DURABILITY, 1) } else it },
                    ) {
                        selected = index
                        updateInventory()
                    }
                }

                button(
                    itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = "취소".component() },
                    from = 6 to 1, to = 6 to 3
                ) {
                    player.closeInventory()
                }

                button(
                    itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = "확인".component() },
                    from = 6 to 7, to = 6 to 9
                ) {
                    if (selected == -1) return@button
                    VoteOptionSelectConfirmUi(vote, selected).open(player)
                }
            }
        }
    }
}