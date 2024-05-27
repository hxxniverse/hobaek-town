package io.github.hxxniverse.hobeaktown.util.extension

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.hasSpace(vararg itemStacks: ItemStack): Boolean {
    // clone inventory
    val checkInventory = Bukkit.createInventory(null, 36, Component.text("레시피 재료 확인"))
    checkInventory.contents = storageContents

    println(checkInventory.contents.map { it?.type to it?.amount })

    // check inventory
    val leftItems = checkInventory.addItem(*itemStacks)

    println(checkInventory.contents.map { it?.type to it?.amount })

    return leftItems.isEmpty()
}

fun Inventory.hasItems(vararg itemStacks: ItemStack): Boolean {
    // clone inventory
    val checkInventory = Bukkit.createInventory(null, 36, Component.text("레시피 재료 확인"))
    checkInventory.contents = storageContents

    // check inventory
    val leftItems = checkInventory.removeItem(*itemStacks)
    return leftItems.isEmpty()
}
