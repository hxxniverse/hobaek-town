package io.github.hxxniverse.hobeaktown.util.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun text(str: Any, color: NamedTextColor = NamedTextColor.WHITE): TextComponent {
    return Component.text(str.toString())
        .color(color)
        .decoration(TextDecoration.ITALIC, false)
}

fun Component.text(str: Any, color: NamedTextColor = NamedTextColor.WHITE): Component {
    return this.append(text(str, color))
}

fun Component.serialize(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}

fun String.text(): Component {
    return text(this)
}

fun String.text(color: NamedTextColor): Component {
    return text(this).color(color)
}

fun Component.appends(vararg components: Component): Component {
    for (component in components) {
        this.append(component)
    }
    return this
}

fun TextComponent.send(player: Player) {
    player.sendMessage(this)
}

fun Component.send(player: Player) {
    player.sendMessage(this)
}

// broadcast
fun Component.broadcast() {
    Bukkit.broadcast(this)
}

fun TextComponent.broadcast() {
    Bukkit.broadcast(this)
}