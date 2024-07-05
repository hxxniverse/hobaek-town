package io.github.hxxniverse.hobeaktown.feature.randombox

import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBox
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.inventory.ItemStack

fun ItemStack.setRandomBox(randomBox: RandomBox): ItemStack = edit {
    addPersistentData("randomBoxId", randomBox.id.toString())
}

fun ItemStack.getRandomBox(): RandomBox? {
    val id = getPersistentData<String>("randomBoxId") ?: return null
    return RandomBox.findById(id.toInt())
}