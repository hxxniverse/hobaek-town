package io.github.hxxniverse.hobeaktown.feature.vote.ui

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class VoteOptionSelectUi(
    private val vote: Vote
) : CustomInventory("VoteOptionSelect", 54) {

    private var selected: Int = -1

    init {
        inventory {
            transaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS))

                button(
                    itemStack = icon { type = Material.PAPER; name = vote.question.text() },
                    from = 4 to 1, to = 6 to 1
                )

                val optionSlots = listOf(
                    4 to 2, 6 to 2,
                    4 to 4, 6 to 4,
                    4 to 6, 6 to 6,
                    4 to 8, 6 to 8
                )

                vote.options.split(",").forEachIndexed { index, option ->
                    button(
                        itemStack = icon { type = Material.PAPER; name = option.text() }.apply {
                            if (index == selected) {
                                edit {
                                    addUnsafeEnchantment(Enchantment.LUCK, 1)
                                }
                            }
                        },
                        index = optionSlots[index],
                    ) {
                        selected = index
                    }
                }

                button(
                    itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = "취소".text() },
                    from = 6 to 1, to = 6 to 3
                ) {
                    player.closeInventory()
                }

                button(
                    itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = "확인".text() },
                    from = 6 to 7, to = 6 to 9
                ) {
                    VoteOptionSelectConfirmUi(vote, selected).open(player)
                }
            }
        }
    }
}