package io.github.hxxniverse.hobeaktown.util.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun text(str: Any): TextComponent {
    return Component.text(str.toString())
        .color(NamedTextColor.WHITE)
        .decoration(TextDecoration.ITALIC, false)
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