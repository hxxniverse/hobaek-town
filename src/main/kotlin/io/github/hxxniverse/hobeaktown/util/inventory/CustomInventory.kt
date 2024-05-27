package io.github.hxxniverse.hobeaktown.util.inventory

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun icon(block: Icon.() -> Unit): ItemStack {
    return Icon().apply(block).build()
}

fun icon(itemStack: ItemStack, block: Icon.() -> Unit): ItemStack {
    return Icon().apply(block).build(itemStack)
}

data class Icon(
    var type: Material = Material.BOOK,
    var name: Component = Component.text(""),
    var lore: List<Component> = listOf(),
    var amount: Int = 1,
) {

    fun build(): ItemStack {
        return ItemStackBuilder(ItemStack(type))
            .setDisplayName(name)
            .setLore(*lore.toTypedArray())
            .setAmount(amount)
            .build()
    }

    fun build(itemStack: ItemStack): ItemStack {
        return ItemStackBuilder(itemStack)
            .setDisplayName(name)
            .setLore(*lore.toTypedArray())
            .setAmount(amount)
            .build()
    }
}

abstract class CustomInventory private constructor(
    private val inventory: Inventory,
) : Listener {

    lateinit var player: Player
    private val clickEvents: MutableMap<Int, (InventoryClickEvent) -> Unit> = mutableMapOf()

    companion object {
        val PREVIOUS_PAGE = ItemStackBuilder(Material.OAK_SIGN).setDisplayName("PREVIOUS_PAGE").build()
        val NEXT_PAGE = ItemStackBuilder(Material.OAK_SIGN).setDisplayName("NEXT_PAGE").build()
        val CLOSE = ItemStackBuilder(Material.RED_WOOL).setDisplayName("CLOSE").build()
        val BACK = ItemStackBuilder(Material.RED_WOOL).setDisplayName("BACK").build()
        val REFRESH = ItemStackBuilder(Material.BLUE_WOOL).setDisplayName("REFRESH").build()
        val MONEY = ItemStackBuilder(Material.GOLD_INGOT).setDisplayName("MONEY").build()
        val CANCEL = ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("CANCEL").build()
        val CONFIRM = ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("CONFIRM").build()
    }

    constructor(
        title: String,
        size: Int,
    ) : this(Bukkit.getServer().createInventory(null, size, Component.text(title)))

    constructor(
        title: String,
        type: InventoryType,
    ) : this(Bukkit.getServer().createInventory(null, type, Component.text(title)))

    fun inventory(block: () -> Unit) {
        content = block
    }

    fun background(itemStack: ItemStack) {
        for (i in 0 until inventory.size) {
            setItem(i, itemStack)
        }
    }

    fun button(itemStack: ItemStack, index: Int, block: (InventoryClickEvent) -> Unit = {}) =
        setItem(index, itemStack, block)

    fun button(itemStack: ItemStack, index: Pair<Int, Int>, block: (InventoryClickEvent) -> Unit = {}) =
        setItem((index.first - 1) + (index.second - 1) * 9, itemStack, block)

    fun button(
        itemStack: ItemStack,
        from: Pair<Int, Int>,
        to: Pair<Int, Int>,
        block: (InventoryClickEvent) -> Unit = {},
    ) {
        for (i in from.first..to.first) {
            for (j in from.second..to.second) {
                setItem((i - 1) + (j - 1) * 9, itemStack, block)
            }
        }
    }

    fun emptySlot(itemStack: ItemStack, block: (InventoryClickEvent) -> Unit = {}) {
        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                setItem(i, itemStack, block)
            }
        }
    }

    fun onInventoryClose(block: InventoryCloseEvent.() -> Unit) {
        onInventoryClose = block
    }

    fun onInventoryOpen(block: InventoryOpenEvent.() -> Unit) {
        onInventoryOpen = block
    }

    fun onPlayerInventoryClick(isCancelled: Boolean = false, block: (InventoryClickEvent) -> Unit) {
        onPlayerInventoryClick = isCancelled to block
    }

    private var content: () -> Unit = {}
    private var onInventoryClose: InventoryCloseEvent.() -> Unit = {}
    private var onInventoryOpen: InventoryOpenEvent.() -> Unit = {}
    private var onPlayerInventoryClick: Pair<Boolean, InventoryClickEvent.() -> Unit> = false to {}

    @EventHandler
    fun onInventoryOpenEvent(event: InventoryOpenEvent) {
        if (event.inventory != inventory) {
            return
        }

        onInventoryOpen(event)
    }

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.clickedInventory == player.inventory && player.openInventory.topInventory == inventory) {
            event.isCancelled = onPlayerInventoryClick.first
            onPlayerInventoryClick.second(event)
            return
        }

        if (event.inventory != inventory) {
            return
        }

        event.isCancelled = true
        clickEvents[event.rawSlot]?.invoke(event)
    }

    @EventHandler
    fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        if (event.inventory != inventory) {
            return
        }

        onInventoryClose(event)
        InventoryCloseEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
        InventoryOpenEvent.getHandlerList().unregister(this)
    }

    open fun refreshContent() {
        inventory.clear()
        clickEvents.clear()
        InventoryClickEvent.getHandlerList().unregister(this@CustomInventory)
        Bukkit.getPluginManager().registerEvents(this@CustomInventory, plugin)
        content()
    }

    fun open(player: Player) {
        this.player = player
        Bukkit.getPluginManager().registerEvents(this@CustomInventory, plugin)
        player.openInventory(inventory)
        content()
    }

    fun openLater(player: Player) {
        this.player = player
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { open(player) }, 1L)
    }

    fun setItem(index: Int, itemStack: ItemStack?, onClick: (InventoryClickEvent) -> Unit = {}) {
        inventory.setItem(index, itemStack)
        clickEvents[index] = onClick
    }

    fun slot(index: Int, toItemStack: ItemStack, onClick: (InventoryClickEvent) -> Unit = {}) {
        setItem(index, toItemStack, onClick)
    }

    fun slot(index: Pair<Int, Int>, toItemStack: ItemStack, onClick: (InventoryClickEvent) -> Unit = {}) {
        setItem((index.first - 1) + (index.second - 1) * 9, toItemStack, onClick)
    }

    fun updateInventory() {
        refreshContent()
    }
}
