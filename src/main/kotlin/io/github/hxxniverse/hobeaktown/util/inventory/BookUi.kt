package io.github.hxxniverse.hobeaktown.util.inventory

import net.kyori.adventure.inventory.Book
import org.bukkit.entity.Player

abstract class BookUi(
    private val builder: Book.Builder.(Player) -> Unit = {},
) {
    fun open(player: Player) {
        player.openBook(Book.builder().apply { builder(player) })
    }
}
