package io.github.hxxniverse.hobeaktown.util

import com.destroystokyo.paper.profile.PlayerProfile
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import kotlin.reflect.jvm.jvmName

class ItemStackDSL(
    itemStack: ItemStack = ItemStack(Material.STONE),
    isWithClone: Boolean = true,
) {

    constructor(material: Material) : this(ItemStack(material))

    private val itemStack = if (isWithClone) itemStack.clone() else itemStack
    var changedItemMeta: ItemMeta = this.itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(this.itemStack.type)

    var displayName: Component? = null
    var customModelData: Int? = null
    var type: Material? = null
    var amount: Int? = null
    var lore: List<Component>? = null
    var enchantments: Map<Enchantment, Int>? = null
    var itemFlags: Set<ItemFlag>? = null
    var unbreakable: Boolean? = null
    var skullMeta: SkullMetaDSL? = null

    inline fun <reified T : Any> addPersistentData(key: String, value: T): ItemStackDSL {
        changedItemMeta.apply { setPersistentData(key, value) }
        return this
    }

    inline fun <reified T : Any> addPersistentData(value: T?): ItemStackDSL {
        if (value == null) {
            return this
        }
        changedItemMeta.apply { setPersistentData(value::class.jvmName, value) }
        return this
    }

    fun build(): ItemStack {
        type?.let { itemStack.type = it }
        skullMeta?.let { changedItemMeta = it.build(changedItemMeta) }
        displayName?.let { changedItemMeta.displayName(it) }
        customModelData?.let { changedItemMeta.setCustomModelData(it) }
        amount?.let { itemStack.amount = it }
        lore?.let { changedItemMeta.lore(it) }
        enchantments?.let {
            it.forEach { (enchantment, level) ->
                changedItemMeta.addEnchant(enchantment, level, true)
            }
        }
        itemFlags?.let {
            it.forEach { flag ->
                changedItemMeta.addItemFlags(flag)
            }
        }
        unbreakable?.let {
            changedItemMeta.isUnbreakable = it
        }
        return itemStack.apply { itemMeta = this@ItemStackDSL.changedItemMeta }
    }

    private fun String.replaceChatColorCode(): String {
        return replace("&", "§")
    }
}

class SkullMetaDSL {
    var playerProfile: PlayerProfile? = null
    var ownerProfile: OfflinePlayer? = null
    var noteBlockSound: NamespacedKey? = null

    fun build(changedItemMeta: ItemMeta): SkullMeta {
        changedItemMeta as SkullMeta
        playerProfile?.let { changedItemMeta.playerProfile = it }
        ownerProfile?.let { changedItemMeta.owningPlayer = it }
        noteBlockSound?.let { changedItemMeta.noteBlockSound = it }
        return changedItemMeta
    }
}

fun ItemStackDSL.skullMeta(init: SkullMetaDSL.() -> Unit): SkullMetaDSL {
    val bannerMetaDSL = SkullMetaDSL()
    bannerMetaDSL.init()
    skullMeta = bannerMetaDSL
    return bannerMetaDSL
}

fun itemStack(
    material: Material = Material.STONE,
    isWithClone: Boolean = true,
    init: ItemStackDSL.() -> Unit
): ItemStack {
    val itemStack = ItemStack(material)
    val finalItemStack = if (isWithClone) itemStack.clone() else itemStack
    val dsl = ItemStackDSL(finalItemStack)
    dsl.init()
    return dsl.build()
}