package io.github.hxxniverse.hobeaktown.feature.economy.util

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat
import kotlinx.serialization.Serializable

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

fun Player.hasMoney(money: Int): Boolean {
    return this.money >= money
}

var Player.cash: Int
    set(value) {
        transaction {
            UserMoney.findOrCreate(uniqueId).cash = value
        }
    }
    get() {
        return transaction {
            UserMoney.findOrCreate(uniqueId).cash
        }
    }

@Serializable
data class PaperMoney(val money: Int)

@Serializable
data class CashCoin(val money: Int)

fun Int.toPaperMoney(): ItemStack {
    return ItemStackBuilder(Material.PAPER)
        .setDisplayName("${DecimalFormat("#,###").format(this)}원")
        .addPersistentData(PaperMoney(this))
        .build()
}

fun ItemStack.toPaperMoney(): PaperMoney? {
    return this.getPersistentData<PaperMoney>()
}

fun ItemStack.setPaperMoney(money: Int): ItemStack {
    return edit {
        addPersistentData(PaperMoney(money))
    }
}

fun ItemStack.isPaperMoney(): Boolean {
    return this.toPaperMoney() != null
}

fun Int.toCashCoin(): ItemStack {
    return ItemStackBuilder(Material.GOLD_NUGGET)
        .setDisplayName("${DecimalFormat("#,###").format(this)}원")
        .addPersistentData(CashCoin(this))
        .build()
}

fun ItemStack.setCashCoin(money: Int): ItemStack {
    return edit {
        addPersistentData(CashCoin(money))
    }
}

fun ItemStack.toCashCoin(): CashCoin? {
    return this.getPersistentData<CashCoin>()
}

fun ItemStack.isCashCoin(): Boolean {
    return this.toCashCoin() != null
}