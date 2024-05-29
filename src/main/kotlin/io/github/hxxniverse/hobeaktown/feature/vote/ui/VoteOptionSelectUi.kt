package io.github.hxxniverse.hobeaktown.feature.vote.ui

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class VoteOptionSelectUi(
    private val vote: Vote,
) : CustomInventory("VoteOptionSelect", 54) {

    private var selected: Int = -1

    init {
        inventory {
            transaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                button(
                    itemStack = icon { type = Material.PAPER; name = vote.question.text() },
                    from = 4 to 1, to = 6 to 1
                )

                val optionSlots = listOf(
                    2 to 2,
                    4 to 2,
                    6 to 2,
                    8 to 2,
                    2 to 4,
                    4 to 4,
                    6 to 4,
                    8 to 4
                )

                vote.options.split(",").filter { it.isNotEmpty() }.forEachIndexed { index, option ->
                    button(
                        itemStack = ItemStackBuilder()
                            .setType(Material.PAPER)
                            .setDisplayName(option.text())
                            .setLore(if (index == selected) listOf("§a선택됨") else listOf("§7선택하지 않음"))
                            .addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                            .build()
                            .let { if (index == selected) it.edit { setAmount(1).addUnsafeEnchantment(Enchantment.DURABILITY, 1) } else it },
                        index = optionSlots[index],
                    ) {
                        selected = index
                        updateInventory()
                    }
                }

                button(
                    itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = "취소".text() },
                    from = 1 to 6, to = 3 to 6
                ) {
                    player.closeInventory()
                }

                button(
                    itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = "확인".text() },
                    from = 7 to 6, to = 9 to 6
                ) {
                    if (selected == -1) return@button
                    VoteOptionSelectConfirmUi(vote, selected).open(player)
                }
            }
        }
    }
}