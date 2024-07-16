package io.github.hxxniverse.hobeaktown.feature.mail

import io.github.hxxniverse.hobeaktown.feature.user.User
import org.bukkit.inventory.ItemStack

fun User.sendMail(sender: User, itemStack: ItemStack) {
    UserMail.new {
        this.user = this@sendMail
        this.sender = sender
        this.itemStack = itemStack
    }
}