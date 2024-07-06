package io.github.hxxniverse.hobeaktown.feature.vote.ui

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.feature.vote.util.setBallot
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class VoteOptionSelectConfirmUi(
    private val vote: Vote,
    private val selected: Int,
) : CustomInventory("VoteOptionSelectConfirm", 54) {
    init {
        inventory {
            transaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                button(
                    itemStack = icon { type = Material.PAPER; name = vote.question.component() },
                    from = 1 to 4, to = 1 to 6
                )

                button(
                    itemStack = icon { type = Material.PAPER; name = vote.options.split(",").filter { it.isNotEmpty() }[selected].component() },
                    from = 3 to 4, to = 3 to 6
                )

                button(
                    itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = "취소".component() },
                    from = 5 to 1, to = 6 to 2
                ) {
                    VoteOptionSelectUi(vote).open(it.whoClicked as Player)

                }

                button(
                    itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = "투표 완료".component() },
                    from = 5 to 8, to = 6 to 9
                ) {
                    val player = it.whoClicked as Player
                    player.closeInventory()
                    player.equipment.itemInMainHand.edit {
                        setDisplayName("투표된 종이")
                        addUnsafeEnchantment(Enchantment.LUCK, 1)
                    }.apply { setBallot(vote.question, selected) }
                        .also { paper -> player.equipment.setItemInMainHand(paper) }
                }
            }
        }
    }
}