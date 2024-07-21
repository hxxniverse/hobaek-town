package io.github.hxxniverse.hobeaktown.util.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

// send info message blue color
fun CommandSender.sendInfoMessage(message: String) {
    sendInfoMessage(message.component())
}

fun CommandSender.sendInfoMessage(message: Component) {
    this.sendMessage("[I] ".component(NamedTextColor.BLUE).append(message))
}

// send error message red color
fun CommandSender.sendErrorMessage(message: String) {
    sendErrorMessage(message.component())
}

fun CommandSender.sendErrorMessage(message: Component) {
    this.sendMessage("[E] ".component(NamedTextColor.RED).append(message))
}

// send success message green color
fun CommandSender.sendSuccessMessage(message: String) {
    sendSuccessMessage(message.component())
}

fun CommandSender.sendSuccessMessage(message: Component) {
    this.sendMessage("[S] ".component(NamedTextColor.GREEN).append(message))
}

// send warning message yellow gold
fun CommandSender.sendWarningMessage(message: String) {
    sendWarningMessage(message.component())
}

fun CommandSender.sendWarningMessage(message: Component) {
    this.sendMessage("[W] ".component(NamedTextColor.GOLD).append(message))
}

// send debug message gray color
fun CommandSender.sendDebugMessage(message: String) {
    sendDebugMessage(message.component())
}

fun CommandSender.sendDebugMessage(message: Component) {
    this.sendMessage("[D] ".component(NamedTextColor.GRAY).append(message))
}