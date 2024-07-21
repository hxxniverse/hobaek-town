package io.github.hxxniverse.hobeaktown.util.inventory

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.coroutine.Hobeak
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import kotlinx.coroutines.*
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
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.lang.Runnable

data class Icon(
    var type: Material = Material.GRAY_STAINED_GLASS_PANE,
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
            .setAmount(itemStack.amount)
            .build()
    }
}

data class Button private constructor(
    var index: Int,
    var itemStack: ItemStack,
    var action: (InventoryClickEvent) -> Unit,
) {
    constructor(index: Pair<Int, Int>, itemStack: ItemStack, action: (InventoryClickEvent) -> Unit) : this(
        (index.second - 1) + (index.first - 1) * 9,
        itemStack,
        action,
    )

    constructor(index: Pair<Int, Int>, itemStack: ItemStack) : this(
        (index.second - 1) + (index.first - 1) * 9,
        itemStack,
        {},
    )
}

abstract class CustomInventory private constructor(
    private val inventory: Inventory,
) : Listener {

    lateinit var player: Player
    private val clickEvents: MutableMap<Int, (InventoryClickEvent) -> Unit> = mutableMapOf()
    var jobs = Job()

    companion object {
        val BACKGROUND = ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("").build()
        val PREVIOUS_PAGE_ICON = ItemStackBuilder(Material.OAK_SIGN).setDisplayName("PREVIOUS_PAGE").build()
        val NEXT_PAGE_ICON = ItemStackBuilder(Material.OAK_SIGN).setDisplayName("NEXT_PAGE").build()
        val CLOSE = ItemStackBuilder(Material.RED_WOOL).setDisplayName("CLOSE").build()
        val BACK = ItemStackBuilder(Material.RED_WOOL).setDisplayName("BACK").build()
        val REFRESH = ItemStackBuilder(Material.BLUE_WOOL).setDisplayName("REFRESH").build()
        val MONEY = ItemStackBuilder(Material.GOLD_INGOT).setDisplayName("MONEY").build()
        val CANCEL_ICON = ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("CANCEL").build()
        val CONFIRM_ICON = ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("CONFIRM").build()
    }

    constructor(
        title: String,
        size: Int,
    ) : this(Bukkit.getServer().createInventory(null, size, Component.text(title)))

    constructor(
        title: String,
        type: InventoryType,
    ) : this(Bukkit.getServer().createInventory(null, type, Component.text(title)))

    fun viewer(): List<Player> {
        return inventory.viewers.filterIsInstance<Player>()
    }

    fun inventory(block: () -> Unit) {
        content = block
    }

    fun background(itemStack: ItemStack) {
        for (i in 0 until inventory.size) {
            setItem(i, itemStack)
        }
    }

    fun icon(block: Icon.() -> Unit = {}): ItemStack {
        return Icon().apply(block).build()
    }

    fun icon(itemStack: ItemStack, block: Icon.() -> Unit = {}): ItemStack {
        return Icon().apply(block).build(itemStack)
    }

    fun item(
        index: Pair<Int, Int>,
        itemStack: ItemStack,
        block: (InventoryClickEvent) -> Unit = {},
    ) {
        setItem((index.second - 1) + (index.first - 1) * 9, itemStack, block)
    }

    fun button(index: Pair<Int, Int>, itemStack: ItemStack, block: (InventoryClickEvent) -> Unit = {}) =
        setItem((index.second - 1) + (index.first - 1) * 9, itemStack, block)

    fun empty(index: Pair<Int, Int>) {
        setItem((index.second - 1) + (index.first - 1) * 9, null) {
            it.isCancelled = false
        }
    }

    fun display(index: Pair<Int, Int>, itemStack: ItemStack) {
        setItem((index.second - 1) + (index.first - 1) * 9, itemStack)
    }

    fun display(from: Pair<Int, Int>, to: Pair<Int, Int>, icon: ItemStack) {
        for (i in from.first..to.first) {
            for (j in from.second..to.second) {
                display(i to j, icon)
            }
        }
    }

    fun empty(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        for (i in from.first..to.first) {
            for (j in from.second..to.second) {
                empty(i to j)
            }
        }
    }

    fun button(
        from: Pair<Int, Int>,
        to: Pair<Int, Int>,
        itemStack: ItemStack,
        block: (InventoryClickEvent) -> Unit = {},
    ) {
        for (i in from.first..to.first) {
            for (j in from.second..to.second) {
                button(i to j, itemStack, block)
            }
        }
    }

    fun runTaskRepeat(interval: Long, repeat: Int = -1, task: suspend () -> Unit): Job {
        return CoroutineScope(Dispatchers.Hobeak + jobs).launch {
            if (repeat == -1) {
                while (true) {
                    task()
                    delay(interval)
                }
            } else {
                repeat(repeat) {
                    task()
                    delay(interval)
                }
            }
        }
    }

    fun runTaskLater(delay: Long, task: suspend () -> Unit): Job {
        return CoroutineScope(Dispatchers.Hobeak + jobs).launch {
            delay(delay)
            task()
        }
    }

    fun runTask(task: suspend () -> Unit): Job {
        return CoroutineScope(Dispatchers.Hobeak + jobs).launch {
            task()
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
        loggedTransaction {
            clickEvents[event.rawSlot]?.invoke(event)
        }
    }

    @EventHandler
    fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        if (event.inventory != inventory) {
            return
        }

        jobs.cancel()
        onInventoryClose(event)
        InventoryCloseEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
        InventoryOpenEvent.getHandlerList().unregister(this)
    }

    open fun update() {
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

    private fun setItem(index: Int, itemStack: ItemStack?, onClick: (InventoryClickEvent) -> Unit = {}) {
        inventory.setItem(index, itemStack)
        clickEvents[index] = loggedTransaction { onClick }
    }

    fun getItem(index: Pair<Int, Int>): ItemStack? {
        return inventory.getItem((index.second - 1) + (index.first - 1) * 9)
    }

    fun getItems(from: Pair<Int, Int>, to: Pair<Int, Int>): List<ItemStack?> {
        val items = mutableListOf<ItemStack?>()
        for (i in from.first..to.first) {
            for (j in from.second..to.second) {
                items.add(getItem(i to j))
            }
        }
        return items
    }

    fun updateInventory() {
        update()
    }
}
