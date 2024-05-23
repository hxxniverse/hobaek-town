package io.github.hxxniverse.hobeaktown.util.extension

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.hasSpace(vararg itemStack: ItemStack): Boolean {
    // clone inventory
    val checkInventory = Bukkit.createInventory(null, 36, Component.text("레시피 재료 확인"))
    checkInventory.addItem(*contents.filterNotNull().toTypedArray())

    // check inventory
    val leftItems = checkInventory.addItem(*itemStack)
    return leftItems.isEmpty()
}

fun Inventory.hasItems(vararg itemStack: ItemStack): Boolean {
    // clone inventory
    val checkInventory = Bukkit.createInventory(null, 36, Component.text("레시피 재료 확인"))
    checkInventory.addItem(*contents.filterNotNull().toTypedArray())

    // check inventory
    val leftItems = checkInventory.removeItem(*itemStack)
    return leftItems.isEmpty()
}
