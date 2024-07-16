package io.github.hxxniverse.hobeaktown.feature.vote.ui

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistories
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistory
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class VoteStatusUi(
    private val vote: Vote,
) : CustomInventory(vote.question, 27) {
    init {
        inventory {
            loggedTransaction {
                vote.options.split(",").forEachIndexed { index, option ->
                    display(1 to index, icon {
                        type = Material.PAPER
                        name = option.component()
                        lore =
                            listOf("§7투표 수: ${VoteHistory.count((VoteHistories.vote eq vote.id) and (VoteHistories.option eq index))}".component())
                    })
                }

                button(
                    3 to 5,
                    itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = "새로고침".component() },
                ) {
                    updateInventory()
                }
            }
        }
    }
}