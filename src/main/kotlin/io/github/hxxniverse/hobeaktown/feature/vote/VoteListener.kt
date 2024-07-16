package io.github.hxxniverse.hobeaktown.feature.vote

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.feature.vote.entity.Votes
import io.github.hxxniverse.hobeaktown.feature.vote.ui.VoteOptionSelectUi
import io.github.hxxniverse.hobeaktown.feature.vote.util.getBallot
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage

class VoteListener : Listener {
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item
        val block = event.clickedBlock

        if (item == null || block == null) return

        if (item.type != Material.PAPER) return

        val votingBooth = loggedTransaction {
            Vote.find { Votes.votingBoothLocation eq block.location }.firstOrNull()
        }

        val voteBox = loggedTransaction {
            Vote.find { Votes.voteBoxLocation eq block.location }.firstOrNull()
        }

        val vote = votingBooth ?: voteBox ?: return

        if (votingBooth != null) {
            // check already voted
            if (loggedTransaction { vote.hasVoted(player.uniqueId) }) {
                player.sendErrorMessage("이미 투표하셨습니다.")
                return
            }
            
            VoteOptionSelectUi(vote).open(player)
            return
        }

        val ballot = item.getBallot() ?: return

        if (ballot.question != vote.question) {
            player.sendErrorMessage("해당 투표 용지는 이 투표에 사용할 수 없습니다.")
            return
        }

        // check already voted
        if (loggedTransaction { vote.hasVoted(player.uniqueId) }) {
            player.sendErrorMessage("이미 투표하셨습니다.")
            return
        }

        item.amount = 0
        loggedTransaction {
            vote.vote(player.uniqueId, ballot.option)
        }
        player.sendInfoMessage("투표가 완료되었습니다.")
    }
}