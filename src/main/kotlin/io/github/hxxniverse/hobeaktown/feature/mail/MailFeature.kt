package io.github.hxxniverse.hobeaktown.feature.mail

import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.feature.user.Users
import io.github.hxxniverse.hobeaktown.util.EntityListPager
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class MailFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction { SchemaUtils.create(UserMails) }
        MailCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class MailCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("mail") {
                executes {
                    MailBoxUi().open(player)
                }
                then("help") {
                    executes {
                        help("help") {
                            command("mail") {
                                description = "메일함을 엽니다."
                            }
                        }
                    }
                }
            }
        }
    }
}

object UserMails : IntIdTable() {
    val receiver = reference("user", Users)
    val sender = reference("sender", Users)
    val itemStack = itemStack("item_stack")
}

class UserMail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserMail>(UserMails)

    var user by User referencedOn UserMails.receiver
    var sender by User referencedOn UserMails.sender
    var itemStack by UserMails.itemStack
}

class MailBoxUi : CustomInventory("택배함", 54) {

    private val boxes = EntityListPager(UserMail) {
        UserMails.receiver eq player.uniqueId
    }

    init {
        inventory {
            loggedTransaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                // previous page button
                if (boxes.hasPreviousPage()) {
                    button(6 to 1, 6 to 2, PREVIOUS_PAGE_ICON) {
                        boxes.previousPage()
                        update()
                    }
                }

                // next page button
                if (boxes.hasNextPage()) {
                    button(6 to 7, 6 to 8, NEXT_PAGE_ICON) {
                        boxes.nextPage()
                        update()
                    }
                }

                // box list 2,2 ~ 5,8 before empty
                empty(2 to 2, 5 to 8)
                boxes.getCurrentPage().forEachIndexed { index, box ->
                    val row = index / 7
                    val col = index % 7
                    button(2 + col to 2 + row, 2 + col to 2 + row, icon(box.itemStack) {
                        name = box.sender.name.component()
                        lore = box.itemStack.itemMeta.lore() ?: emptyList()
                    }) {
                        // add inventory with check
                        if (player.inventory.firstEmpty() != -1) {
                            player.inventory.addItem(box.itemStack)
                            box.delete()
                            update()
                        }
                    }
                }
            }
        }
    }
}