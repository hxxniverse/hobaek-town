package io.github.hxxniverse.hobeaktown.feature.vote.entity

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock.Companion.referrersOn
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistory
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stocks
import io.github.hxxniverse.hobeaktown.util.database.location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import java.util.UUID

object Votes : IntIdTable() {
    val question = varchar("question", 255)
    val options = varchar("options", 255)
    val voteBoxLocation = location("vote_box_location").nullable()
    val votingBoothLocation = location("voting_booth_location").nullable()
    val isVoting = bool("is_voting").default(false)
}

class Vote(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Vote>(Votes) {
        fun new(
            question: String,
            options: List<String>,
        ) = loggedTransaction {
            Vote.new {
                this.question = question
                this.options = options.joinToString(",")
            }
        }
    }

    var question by Votes.question
    var options by Votes.options
    var voteBoxLocation by Votes.voteBoxLocation
    var votingBoothLocation by Votes.votingBoothLocation
    var isVoting by Votes.isVoting

    fun vote(playerUUID: UUID, option: Int) = loggedTransaction {
        VoteHistory.new(
            voter = playerUUID,
            vote = this@Vote,
            option = option
        )
    }

    fun hasVoted(uniqueId: UUID): Boolean {
        return loggedTransaction {
            VoteHistory.find { (VoteHistories.voter eq uniqueId) and (VoteHistories.vote eq this@Vote.id) }.firstOrNull() != null
        }
    }
}