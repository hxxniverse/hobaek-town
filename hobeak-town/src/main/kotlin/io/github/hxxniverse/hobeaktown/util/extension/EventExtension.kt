package io.github.hxxniverse.hobeaktown.util.extension

import io.papermc.paper.event.player.AbstractChatEvent
import net.kyori.adventure.text.TextComponent

val AbstractChatEvent.contentMessage: String
    get() = (message() as? TextComponent)?.content() ?: ""