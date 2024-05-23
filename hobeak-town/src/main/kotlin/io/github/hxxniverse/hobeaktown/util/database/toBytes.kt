package io.github.hxxniverse.hobeaktown.util.database

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun ItemStack.toBytes(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    val dataOutput = BukkitObjectOutputStream(outputStream)
    dataOutput.writeObject(this)
    dataOutput.close()
    return outputStream.toByteArray()
}

fun ByteArray.toItemStack(): ItemStack {
    val inputStream = ByteArrayInputStream(this)
    val dataInput = BukkitObjectInputStream(inputStream)
    return dataInput.readObject() as ItemStack
}