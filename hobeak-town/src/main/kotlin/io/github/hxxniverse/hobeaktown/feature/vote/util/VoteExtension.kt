package io.github.hxxniverse.hobeaktown.feature.vote.util

import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import org.bukkit.inventory.ItemStack

data class Ballot(
    val question: String,
    val option: Int
)

fun ItemStack.setBallot(
    question: String,
    option: Int
) {
    setPersistentData("vote", Ballot(question, option))
}

fun ItemStack.getBallot(): Ballot? {
    return getPersistentData<Ballot>("vote")
}