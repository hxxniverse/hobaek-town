package io.github.hxxniverse.hobeaktown.feature.vote.ui

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistories
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistory
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class VoteStatusUi(
    private val vote: Vote,
) : CustomInventory(vote.question, 27) {
    init {
        inventory {
            transaction {
                vote.options.split(",").filter { it.isNotEmpty() }.forEachIndexed { index, option ->
                    setItem(index, icon {
                        type = Material.PAPER
                        name = option.text()
                        lore = listOf("§7투표 수: ${VoteHistory.count((VoteHistories.vote eq vote.id) and (VoteHistories.option eq index))}".text())
                    })
                }

                button(
                    itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = "새로고침".text() },
                    index = 5 to 3
                ) {
                    updateInventory()
                }
            }
        }
    }
}