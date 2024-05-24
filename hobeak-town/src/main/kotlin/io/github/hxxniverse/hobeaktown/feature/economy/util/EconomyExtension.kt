package io.github.hxxniverse.hobeaktown.feature.economy.util

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat

var Player.money: Int
    set(value) {
        transaction {
            UserMoney.findOrCreate(uniqueId).money = value
        }
    }
    get() {
        return transaction {
            UserMoney.findOrCreate(uniqueId).money
        }
    }

data class PaperMoney(val money: Int)

fun Int.toPaperMoney(): ItemStack {
    return ItemStackBuilder(Material.PAPER)
        .setDisplayName("${DecimalFormat("#,###").format(this)}원")
        .addPersistentData(PaperMoney(this))
        .build()
}

fun ItemStack.toPaperMoney(): PaperMoney? {
    return this.getPersistentData<PaperMoney>()
}

fun ItemStack.isPaperMoney(): Boolean {
    return this.toPaperMoney() != null
}