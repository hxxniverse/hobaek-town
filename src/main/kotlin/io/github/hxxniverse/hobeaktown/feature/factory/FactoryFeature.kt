package io.github.hxxniverse.hobeaktown.feature.factory

import io.github.hxxniverse.hobeaktown.util.EntityListPager
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * ui
 */
class FactoryFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {

    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class FactoryListener : Listener {
    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        val player = event.player
        val itemStack = event.itemInHand.clone().apply { amount = 1 }

        val factoryMachine = FactoryMachine.find { FactoryMachines.itemStack eq itemStack }.firstOrNull()

        if (factoryMachine == null) {
            return
        }

        PlacedFactoryMachine.new {
            machine = factoryMachine
            location = event.block.location
        }
    }

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val block = event.block

        val placedFactoryMachine =
            PlacedFactoryMachine.find { PlacedFactoryMachines.location eq block.location }.firstOrNull()

        if (placedFactoryMachine == null) {
            return
        }

        placedFactoryMachine.delete()
        event.isDropItems = false
        block.world.dropItemNaturally(block.location, placedFactoryMachine.machine.itemStack)
    }

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return

        val placedFactoryMachine =
            PlacedFactoryMachine.find { PlacedFactoryMachines.location eq block.location }.firstOrNull()

        if (placedFactoryMachine == null) {
            return
        }

        if (placedFactoryMachine.recipe == null) {
            FactoryMachineMaterialInputUI(placedFactoryMachine).open(player)
        } else {
            if (placedFactoryMachine.completeTime == null || placedFactoryMachine.leftTime.isAfter(LocalDateTime.now())) {
                FactoryMachineCreatingUI(placedFactoryMachine).open(player)
            } else {
                FactoryMachineCompleteUI(placedFactoryMachine).open(player)
            }
        }
    }
}

class FactoryCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("factory") {
                executes { FactoryMachineSelectUI().open(player) }
                then("create") {
                    then("name" to string()) {
                        executes { it ->
                            val name: String by it

                            if (player.inventory.itemInMainHand.type.isAir) {
                                player.sendMessage("아이템을 들고 있어야 합니다.")
                                return@executes
                            }

                            if (FactoryMachine.find { FactoryMachines.name eq name }.empty()) {
                                FactoryMachine.new {
                                    this.name = name
                                    this.itemStack = player.inventory.itemInMainHand
                                }.also {
                                    it.itemStack = it.itemStack.edit {
                                        addPersistentData("factoryMachineId", it.id.value)
                                    }
                                }
                                player.sendMessage("생성되었습니다.")
                            } else {
                                player.sendMessage("이미 존재하는 이름입니다.")
                            }
                        }
                    }
                }
                then("delete") {
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            val machine = FactoryMachine.find { FactoryMachines.name eq name }.firstOrNull()

                            if (machine == null) {
                                player.sendMessage("존재하지 않는 이름입니다.")
                                return@executes
                            }

                            machine.delete()
                            player.sendMessage("삭제되었습니다.")
                        }
                    }
                }
                then("manager") {
                    executes {
                        FactoryMachineCreateRecipeUI().open(player)
                    }
                }
            }
        }
    }
}

object PlacedFactoryMachines : IntIdTable() {
    val machine = reference("machine", FactoryMachines)
    val location = location("location")
    val output = optReference("output", FactoryMachineOutputs)
    val completeTime = datetime("complete_time").nullable()
}

class PlacedFactoryMachine(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlacedFactoryMachine>(PlacedFactoryMachines)

    var machine by FactoryMachine referencedOn PlacedFactoryMachines.machine
    var location by PlacedFactoryMachines.location
    var recipe by FactoryMachineOutput optionalReferencedOn PlacedFactoryMachines.output
    var completeTime by PlacedFactoryMachines.completeTime

    val leftTime: LocalDateTime
        get() = completeTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
        } ?: LocalDateTime.now()


    fun craft(items: List<ItemStack?>) {
        val inputs = items.filterNotNull()

        val recipe = machine.recipes.firstOrNull {
            val materials: List<ItemStack> =
                it.materials.map { material -> material.itemStack.edit { setAmount(material.amount) } }

            materials == inputs
        }

        if (recipe == null) {
            throw Exception("레시피가 존재하지 않습니다.")
        }

        this.recipe = recipe
        this.completeTime = LocalDateTime.now().plusSeconds(10)
    }

}

object FactoryMachines : IntIdTable() {
    val name = text("name")
    val itemStack = itemStack("item_stack")
}

class FactoryMachine(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FactoryMachine>(FactoryMachines)

    var name by FactoryMachines.name
    var itemStack by FactoryMachines.itemStack
    val recipes by FactoryMachineOutput referrersOn FactoryMachineOutputs.machine
    val materials by FactoryMachineMaterial referrersOn FactoryMachineMaterials.machine

    fun createOrUpdateOutputRecipe(outputMaterials: List<ItemStack>, outputItemStack: ItemStack) {
        val recipe = FactoryMachineOutput.find { FactoryMachineOutputs.machine eq id }.firstOrNull()

        if (recipe == null) {
            val output = FactoryMachineOutput.new {
                machine = this@FactoryMachine.id
                this.output = outputItemStack.type.name
            }
            outputMaterials.forEach {
                FactoryMachineOutputMaterial.new {
                    this.recipe = output.id
                    this.itemStack = it
                    this.amount = it.amount
                }
            }
        } else {
            recipe.output = outputItemStack.type.name
            recipe.materials.forEach { it.delete() }
            outputMaterials.forEach {
                FactoryMachineOutputMaterial.new {
                    this.recipe = recipe.id
                    this.itemStack = it
                    this.amount = it.amount
                }
            }
        }
    }

    fun updateRecipe(materials: List<ItemStack>) {
        this.materials.forEach { it.delete() }
        materials.forEach {
            FactoryMachineMaterial.new {
                this.machine = this@FactoryMachine.id
                this.input = it
                this.amount = it.amount
            }
        }
    }
}

fun FactoryMachine.toItemStack(): ItemStack {
    return itemStack.edit {
        addPersistentData("factoryMachineId", id)
    }
}

fun ItemStack.toFactoryMachine(): FactoryMachine? {
    return getPersistentData<Int>("factoryMachineId")?.let {
        FactoryMachine.findById(it)
    }
}


object FactoryMachineMaterials : IntIdTable() {
    val machine = reference("machine", FactoryMachines)
    val input = itemStack("input")
    val amount = integer("amount")
}

class FactoryMachineMaterial(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FactoryMachineMaterial>(FactoryMachineMaterials)

    var machine by FactoryMachineMaterials.machine
    var input by FactoryMachineMaterials.input
    var amount by FactoryMachineMaterials.amount
}

object FactoryMachineOutputs : IntIdTable() {
    val machine = reference("machine", FactoryMachines)
    val output = text("output")
}

class FactoryMachineOutput(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FactoryMachineOutput>(FactoryMachineOutputs)

    var machine by FactoryMachineOutputs.machine
    val materials by FactoryMachineOutputMaterial referrersOn FactoryMachineOutputMaterials.recipe
    var output by FactoryMachineOutputs.output
}

object FactoryMachineOutputMaterials : IntIdTable() {
    val recipe = reference("recipe", FactoryMachineOutputs)
    val itemStack = itemStack("item_stack")
    val amount = integer("amount")
}

class FactoryMachineOutputMaterial(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FactoryMachineOutputMaterial>(FactoryMachineOutputMaterials)

    var recipe by FactoryMachineOutputMaterials.recipe
    var itemStack by FactoryMachineOutputMaterials.itemStack
    var amount by FactoryMachineOutputMaterials.amount
}

/**
 * factory machine select ui
 *
 * background
 *
 * 2,2 2,4 2,6 2,8 = machines
 * 3,1 = previous
 * 3,9 = next
 */
class FactoryMachineSelectUI : CustomInventory("Factory Machine Select", 27) {

    private val pager = EntityListPager(FactoryMachine, 4)

    init {
        inventory {
            background(BACKGROUND)

            pager.getCurrentPage().forEachIndexed { index, factoryMachine ->
                item(2 to index + 2, factoryMachine.itemStack) {
                    FactoryMachineCreateUI(factoryMachine).open(player)
                }
            }

            item(3 to 1, PREVIOUS_PAGE_ICON) {
                pager.previousPage()
                update()
            }

            item(3 to 9, NEXT_PAGE_ICON) {
                pager.nextPage()
                update()
            }
        }
    }
}

/**
 * factory machine create ui
 *
 * background
 *
 * 2,2 = select machine
 * 1,4 ~ 3,6 = empty
 * 2,8 = confirm
 */
class FactoryMachineCreateUI(
    private val factoryMachine: FactoryMachine
) : CustomInventory("Factory Machine Create", 27) {
    init {
        inventory {
            background(BACKGROUND)

            display(2 to 2, icon(factoryMachine.itemStack) {
                name = factoryMachine.name.component()
            })

            empty(1 to 4, 3 to 6)

            item(2 to 8, CONFIRM_ICON) {
                // TODO 제작하는 로직
            }
        }
    }
}

/**
 * factory machine material input ui
 *
 * background
 *
 * 2,2 = select machine
 * 2,4 ~ 2,6 = input slot
 * 2,8 = confirm
 */
class FactoryMachineMaterialInputUI(
    private val placedFactoryMachine: PlacedFactoryMachine
) : CustomInventory("Factory Machine Material Input", 27) {
    init {
        inventory {
            item(2 to 2, icon(placedFactoryMachine.machine.itemStack) {
                name = placedFactoryMachine.machine.name.component()
            })

            empty(2 to 4, 2 to 6)

            item(2 to 8, CONFIRM_ICON) {
                try {
                    placedFactoryMachine.craft(getItems(2 to 4, 2 to 6))
                    FactoryMachineCreatingUI(placedFactoryMachine).open(player)
                    player.sendInfoMessage("제작을 시작합니다.")
                } catch (e: Exception) {
                    getItems(2 to 4, 2 to 6).forEach { item -> item?.let { player.inventory.addItem(it) } }
                    player.sendErrorMessage((e.message ?: "").component())
                }
            }
        }
    }
}

/**
 * factory machine creating ui
 *
 * background
 *
 * 2,5 = progress bar
 */
class FactoryMachineCreatingUI(
    private val placedFactoryMachine: PlacedFactoryMachine
) : CustomInventory("Factory Machine Creating", 27) {
    init {
        inventory {
            background(BACKGROUND)

            item(2 to 5, icon(ItemStack(Material.CLOCK)) {
                name = "남은시간: ${placedFactoryMachine.leftTime.format(DateTimeFormatter.ISO_TIME)}".component()
            })
        }

        onInventoryOpen {
            runTaskLater(
                placedFactoryMachine.leftTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now()
                    .toEpochSecond(ZoneOffset.UTC)
            ) {
                FactoryMachineCompleteUI(placedFactoryMachine).open(player as Player)
                player.sendInfoMessage("제작이 완료되었습니다.")
            }
        }
    }
}

/**
 * factory machine complete ui
 *
 * background
 *
 * 2,2 = select machine
 * 2,5 = output slot
 * 2,8 = confirm
 */
class FactoryMachineCompleteUI(
    private val placedFactoryMachine: PlacedFactoryMachine
) : CustomInventory("Factory Machine Complete", 27) {
    init {
        inventory {
            background(BACKGROUND)

            item(2 to 2, icon(placedFactoryMachine.machine.itemStack) {
                name = placedFactoryMachine.machine.name.component()
            })

            item(
                2 to 5,
                icon(placedFactoryMachine.recipe?.output?.let { ItemStack(Material.valueOf(it)) }
                    ?: ItemStack(Material.AIR)) {
                    name = placedFactoryMachine.recipe?.output?.component() ?: "없음".component()
                })

            item(2 to 8, CONFIRM_ICON) {
                placedFactoryMachine.recipe?.let {
                    placedFactoryMachine.recipe = null
                    placedFactoryMachine.completeTime = null
                    FactoryMachineMaterialInputUI(placedFactoryMachine).open(player)
                }
            }
        }
    }
}

/**
 * factory machine create recipe ui
 *
 * background
 *
 * 1,2 = 1st machine's output recipe
 * 2,2 = 1st machine
 * 3,2 = 1st machine's recipe
 */
class FactoryMachineCreateRecipeUI : CustomInventory("Factory Machine Create Recipe", 27) {

    private val pager = EntityListPager(FactoryMachine, 4)

    init {
        inventory {
            background(BACKGROUND)

            pager.getCurrentPage().forEachIndexed { index, factoryMachine ->
                item(1 to index + 2, icon {
                    type = Material.GREEN_STAINED_GLASS_PANE
                    name = "Output Recipe".component()
                }) {
                    FactoryMachineOutputRecipeSetUI(factoryMachine).open(player)
                }

                display(2 to index + 2, factoryMachine.itemStack)

                item(3 to index + 2, icon {
                    type = Material.RED_STAINED_GLASS_PANE
                    name = "Factory Machine Recipe".component()
                }) {
                    FactoryMachineCreateRecipeSetUI(factoryMachine).open(player)
                }
            }
        }
    }
}


/**
 * factory machine output recipe ui
 *
 * background
 *
 * 1,5 = select machine
 * 2,2 ~ 2,4 = input slot
 * 2,6 = output slot
 * 2,8 = confirm
 * 3,1 = next page
 * 3,9 = previous page
 */
class FactoryMachineOutputRecipeSetUI(
    private val factoryMachine: FactoryMachine
) : CustomInventory("Factory Machine Output Recipe Set", 27) {

    private val pager = EntityListPager(
        FactoryMachineOutput,
        1
    ) { FactoryMachineOutputs.machine eq factoryMachine.id }

    init {
        inventory {
            background(BACKGROUND)

            item(1 to 5, icon(factoryMachine.itemStack) {
                name = factoryMachine.name.component()
            })

            empty(2 to 2, 2 to 4)
            empty(2 to 6)

            item(2 to 8, CONFIRM_ICON) {
                val materials = getItems(2 to 2, 2 to 4).filterNotNull()
                val output = getItem(2 to 6)

                if (materials.isEmpty() || output == null) {
                    player.sendErrorMessage("아이템을 입력해주세요.")
                    return@item
                }

                factoryMachine.createOrUpdateOutputRecipe(materials, output)
            }

            item(3 to 1, NEXT_PAGE_ICON) {
                pager.nextPage()
                update()
            }

            item(3 to 9, PREVIOUS_PAGE_ICON) {
                pager.previousPage()
                update()
            }
        }
    }
}

/** factory machine create recipe ui
 *
 * background
 *
 * 2,2 = select machine
 * 1,4 ~ 3,6 = empty
 * 2,8 = confirm
 */
class FactoryMachineCreateRecipeSetUI(
    private val factoryMachine: FactoryMachine
) : CustomInventory("Factory Machine Create Recipe Set", 27) {
    init {
        inventory {
            background(BACKGROUND)

            empty(1 to 4, 3 to 6)

            item(2 to 8, CONFIRM_ICON) {
                val materials = getItems(1 to 4, 3 to 6).filterNotNull()

                if (materials.isEmpty()) {
                    player.sendErrorMessage("아이템을 입력해주세요.")
                    return@item
                }

                factoryMachine.updateRecipe(materials)
            }
        }
    }
}