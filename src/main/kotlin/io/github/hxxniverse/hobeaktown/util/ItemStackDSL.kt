package io.github.hxxniverse.hobeaktown.util

import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import kotlin.reflect.jvm.jvmName

class ItemStackDSL(
    itemStack: ItemStack = ItemStack(Material.STONE),
    isWithClone: Boolean = true,
) {

    constructor(material: Material) : this(ItemStack(material))

    private val itemStack = if (isWithClone) itemStack.clone() else itemStack
    var changedItemMeta: ItemMeta = this.itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(this.itemStack.type)

    fun setDisplayName(displayName: String): ItemStackDSL {
        changedItemMeta.displayName(
            Component.text(displayName)
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
        )
        return this
    }

    fun setDisplayName(component: Component): ItemStackDSL {
        changedItemMeta.displayName(component.decoration(TextDecoration.ITALIC, false))
        return this
    }

    fun setCustomModelData(customModelData: Int): ItemStackDSL {
        changedItemMeta.setCustomModelData(customModelData)
        return this
    }

    fun setType(material: Material): ItemStackDSL {
        itemStack.type = material
        changedItemMeta = itemStack.itemMeta
        return this
    }

    fun setAmount(value: Int): ItemStackDSL {
        itemStack.amount = value
        return this
    }

    fun addAmount(value: Int): ItemStackDSL {
        itemStack.amount += value
        return this
    }

    fun addLore(value: String): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply {
                add(
                    Component.text(value)
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                )
            }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun addLore(vararg value: String): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply {
                value.forEach {
                    add(
                        Component.text(it)
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    )
                }
            }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun addLore(value: List<String>): ItemStackDSL {
        addLore(*value.map { it }.toTypedArray())
        return this
    }

    fun removeLore(index: Int): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply { removeAt(index) }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun editLore(index: Int, value: String): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply {
                set(
                    index,
                    Component.text(value)
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                )
            }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun setLore(vararg lore: String): ItemStackDSL {
        changedItemMeta.lore(
            lore.map {
                Component.text(it)
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false)
            },
        )
        return this
    }

    fun setLore(lore: List<String>): ItemStackDSL {
        setLore(*lore.toTypedArray())
        return this
    }

    // # Component

    fun addLore(value: Component): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply {
                add(
                    value.color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                )
            }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun addLore(vararg value: Component): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply { value.forEach { add(it) } }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun editLore(index: Int, value: Component): ItemStackDSL {
        (changedItemMeta.lore() ?: mutableListOf())
            .apply { set(index, value) }
            .also { changedItemMeta.lore(it) }
        return this
    }

    fun setLore(vararg lore: Component): ItemStackDSL {
        changedItemMeta.lore(lore.toList().map { it.decoration(TextDecoration.ITALIC, false) })
        return this
    }

    fun addUnsafeEnchantment(enchantment: Enchantment, level: Int): ItemStackDSL {
        itemStack.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun addEnchantment(enchantment: Enchantment, level: Int): ItemStackDSL {
        itemStack.addEnchantment(enchantment, level)
        return this
    }

    fun removeEnchantment(enchantment: Enchantment): ItemStackDSL {
        itemStack.removeEnchantment(enchantment)
        return this
    }

    fun addItemFlags(vararg itemFlags: ItemFlag): ItemStackDSL {
        changedItemMeta.addItemFlags(*itemFlags)
        return this
    }

    fun removeItemFlags(vararg itemFlags: ItemFlag): ItemStackDSL {
        changedItemMeta.removeItemFlags(*itemFlags)
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemStackDSL {
        changedItemMeta.isUnbreakable = unbreakable
        return this
    }

    fun setBannerMeta(block: BannerMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as BannerMeta).apply(block)
        return this
    }

    fun setEnchantmentStorageMeta(block: EnchantmentStorageMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as EnchantmentStorageMeta).apply(block)
        return this
    }

    fun setLeatherArmorMeta(block: LeatherArmorMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as LeatherArmorMeta).apply(block)
        return this
    }

    fun setPotionMeta(block: PotionMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as PotionMeta).apply(block)
        return this
    }

    fun setSkullMeta(block: SkullMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as SkullMeta).apply(block)
        return this
    }

    fun setSuspiciousStewMeta(block: SuspiciousStewMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as SuspiciousStewMeta).apply(block)
        return this
    }

    fun setTropicalFishBucketMeta(block: TropicalFishBucketMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as TropicalFishBucketMeta).apply(block)
        return this
    }

    fun setBookMeta(block: BookMeta.() -> Unit): ItemStackDSL {
        (changedItemMeta as BookMeta).apply(block)
        return this
    }

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
        return itemStack.apply { itemMeta = this@ItemStackDSL.changedItemMeta }
    }

    private fun String.replaceChatColorCode(): String {
        return replace("&", "§")
    }
}

fun itemStack(material: Material = Material.STONE, isWithClone: Boolean = true, init: ItemStackDSL.() -> Unit): ItemStack {
    val itemStack = ItemStack(material)
    val finalItemStack = if (isWithClone) itemStack.clone() else itemStack
    val dsl = ItemStackDSL(finalItemStack)
    dsl.init()
    return dsl.build()
}