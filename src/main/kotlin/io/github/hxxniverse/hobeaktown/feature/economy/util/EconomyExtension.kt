package io.github.hxxniverse.hobeaktown.feature.economy.util

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat

var User.money: Int
    set(value) {
        transaction { UserMoney.findOrCreate(this@money.id.value).money = value }
    }
    get() = transaction { UserMoney.findOrCreate(this@money.id.value).money }

fun User.hasMoney(money: Int): Boolean {
    return this.money >= money
}

var User.cash: Int
    set(value) {
        transaction { UserMoney.findOrCreate(this@cash.id.value).cash = value }
    }
    get() {
        return transaction { UserMoney.findOrCreate(this@cash.id.value).cash }
    }

@Serializable
data class PaperMoney(val money: Int)

@Serializable
data class CashCoin(val money: Int)

fun Int.toPaperMoney(material: Material = Material.PAPER): ItemStack {
    return ItemStackBuilder(material)
        .setDisplayName("${DecimalFormat("#,###").format(this)} 원")
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

fun Int.toCashCoin(material: Material = Material.GOLD_NUGGET): ItemStack {
    return ItemStackBuilder(material)
        .setDisplayName("${DecimalFormat("#,###").format(this)} 코인")
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