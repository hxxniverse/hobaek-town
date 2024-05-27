package io.github.hxxniverse.hobeaktown.feature.vote.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.UUID

object VoteHistories : IntIdTable() {
    val voter = uuid("uuid")
    val vote = reference("vote", Votes)
    val option = integer("option")
}

class VoteHistory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VoteHistory>(VoteHistories) {
        fun new(
            voter: UUID,
            vote: Vote,
            option: Int,
        ) = VoteHistory.new {
            this.voter = voter
            this.vote = vote
            this.option = option
        }
    }

    var voter by VoteHistories.voter
    var vote by Vote referencedOn VoteHistories.vote
    var option by VoteHistories.option
}