package io.github.hxxniverse.hobeaktown.feature.auction

import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.mail.sendMail
import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.feature.user.Users
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.appends
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.pretty
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.wesjd.anvilgui.AnvilGUI
import net.wesjd.anvilgui.AnvilGUI.ResponseAction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.function.UnaryOperator

class AuctionFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.createMissingTablesAndColumns(Auctions, AuctionItems, AuctionBids)
        }

        plugin.server.pluginManager.registerEvents(AuctionListener(), plugin)
        AuctionCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class AuctionListener : Listener {
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val auction =
            transaction { Auction.find { Auctions.location eq event.clickedBlock?.location }.firstOrNull() } ?: return

        if (!auction.isOpened) {
            player.sendMessage("경매장이 오픈되어 있지 않습니다.")
            return
        }

        AuctionItemListUi(auction).open(player)
    }
}

class AuctionCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("경매장") {
                then("도움말") {
                    executes {
                        help("경매장") {
                            command("경매장 생성 <type> <hour>") {
                                description = "경매장 생성"
                                subDescription = "<type> ${AuctionType.entries.joinToString(", ")}"
                            }
                            command("경매장 설정") {
                                description = "경매장 설정"
                            }
                            command("경매장 블럭설정") {
                                description = "경매장 블럭 설정"
                                subDescription = "바라보는 블럭을 경매장 블럭으로 설정합니다."
                            }
                            command("경매장 시작") {
                                description = "경매장 시작"
                            }
                            command("경매장 종료") {
                                description = "경매장 종료"
                            }
                        }
                    }
                }
                then("생성") {
                    then("type" to dynamicByEnum(EnumSet.allOf(AuctionType::class.java))) {
                        then("hour" to int()) {
                            executes {
                                loggedTransaction {
                                    val type: AuctionType by it
                                    val hour: Int by it

                                    // 경매장 생성되어 있는지 체크
                                    val myAuction = Auction.find { Auctions.owner eq player.user.id }.firstOrNull()
                                    if (myAuction != null) {
                                        player.sendMessage("이미 경매장이 생성되어 있습니다.")
                                        return@loggedTransaction
                                    }

                                    // 경매장 생성
                                    Auction.new {
                                        name = "경매장"
                                        owner = player.user.id
                                        expirationTime = LocalDateTime.now().plusHours(hour.toLong())
                                        this.type = type

                                        player.sendMessage("경매장이 생성되었습니다.")
                                    }
                                }
                            }
                        }
                    }
                }
                then("설정") {
                    executes {
                        loggedTransaction {
                            val myAuction = Auction.find { Auctions.owner eq player.user.id }.firstOrNull()
                            if (myAuction == null) {
                                player.sendMessage("경매장이 생성되어 있지 않습니다.")
                                return@loggedTransaction
                            }

                            if (myAuction.isExpired()) {
                                player.sendMessage("경매장이 종료되었습니다.")
                                return@loggedTransaction
                            }

                            if (myAuction.isOpened) {
                                player.sendMessage("경매장이 이미 오픈되어 있습니다.")
                                return@loggedTransaction
                            }

                            AuctionSettingUi(myAuction).open(player)
                        }
                    }
                }
                then("블럭설정") {
                    executes {
                        loggedTransaction {
                            // 경매장 블럭설정
                            val block = player.getTargetBlockExact(10)

                            if (block == null) {
                                player.sendMessage("블럭을 찾을 수 없습니다.")
                                return@loggedTransaction
                            }

                            val myAuction = Auction.find { Auctions.owner eq player.user.id }.firstOrNull()

                            if (myAuction == null) {
                                player.sendMessage("경매장이 생성되어 있지 않습니다.")
                                return@loggedTransaction
                            }

                            myAuction.location = block.location
                            player.sendMessage("경매장 블럭이 설정되었습니다.")
                        }
                    }
                }
                then("시작") {
                    executes {
                        loggedTransaction {
                            val myAuction = Auction.find { Auctions.owner eq player.user.id }.firstOrNull()

                            if (myAuction == null) {
                                player.sendMessage("경매장이 생성되어 있지 않습니다.")
                                return@loggedTransaction
                            }

                            if (myAuction.isExpired()) {
                                player.sendMessage("경매장이 종료되었습니다.")
                                return@loggedTransaction
                            }

                            if (myAuction.isOpened) {
                                player.sendMessage("경매장이 이미 오픈되어 있습니다.")
                                return@loggedTransaction
                            }

                            if (myAuction.items.empty()) {
                                player.sendMessage("경매장 아이템이 없습니다.")
                                return@loggedTransaction
                            }

                            if (myAuction.location == null) {
                                player.sendMessage("경매장 블럭이 설정되어 있지 않습니다.")
                                return@loggedTransaction
                            }

                            myAuction.start()
                            player.sendMessage("경매장이 오픈되었습니다.")
                        }
                    }
                }
                then("종료") {
                    executes {
                        loggedTransaction {
                            val myAuction = Auction.find { Auctions.owner eq player.user.id }.firstOrNull()

                            if (myAuction == null) {
                                player.sendMessage("경매장이 생성되어 있지 않습니다.")
                                return@loggedTransaction
                            }

                            if (!myAuction.isOpened) {
                                player.sendMessage("경매장이 오픈되어 있지 않습니다.")
                                return@loggedTransaction
                            }

                            myAuction.end()
                            player.sendMessage("경매장이 종료되었습니다.")
                        }
                    }
                }
            }
        }
    }
}

enum class AuctionType {
    NORMAL,
    BLIND
}

object Auctions : IntIdTable() {
    val name = varchar("name", 255)
    val owner = reference("owner", Users)
    val expirationTime = datetime("expiration_time")
    val type = enumeration("type", AuctionType::class)
    val location = location("location").nullable()
    val isOpened = bool("is_open").default(false)
}

class Auction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Auction>(Auctions)

    var name by Auctions.name
    var owner by Auctions.owner
    var expirationTime by Auctions.expirationTime
    var type by Auctions.type
    var location by Auctions.location
    var isOpened by Auctions.isOpened

    val items by AuctionItem referrersOn AuctionItems.auction
    val bids by AuctionBid referrersOn AuctionBids.auction

    fun isExpired(): Boolean {
        return expirationTime.isBefore(LocalDateTime.now())
    }

    fun isOwner(user: User): Boolean {
        return owner == user.id
    }

    private fun getItemByHighestBid(): Map<AuctionItem, AuctionBid?> {
        return items.associateWith { item ->
            bids.filter { it.item == item }.maxByOrNull(AuctionBid::amount)
        }
    }

    fun getBids(user: User): List<AuctionBid> {
        return bids.filter { it.user == user }
    }

    fun getBids(): List<AuctionBid> {
        return bids.toList()
    }

    fun getItems(): List<AuctionItem> {
        return items.toList()
    }

    fun addItems(items: List<ItemStack>) {
        clearItems()
        items.forEach { item ->
            AuctionItem.new {
                this.auction = this@Auction
                this.itemStack = item
            }
        }
    }

    fun addBid(user: User, auctionItem: AuctionItem, amount: Int) {
        AuctionBid.new {
            this.auction = this@Auction
            this.user = user
            this.item = auctionItem
            this.amount = amount
        }
    }

    private fun clearBids() {
        bids.forEach(AuctionBid::delete)
    }

    private fun clearItems() {
        items.forEach(AuctionItem::delete)
    }

    private fun clear() {
        clearBids()
        clearItems()
    }

    fun start() {
        // 경매 시작
        isOpened = true

        Bukkit.broadcast("* 경매가 시작되었습니다! 장소: ${location?.pretty()}".component())
    }

    fun end() {
        // 경매 종료
        isOpened = false

        val messages = mutableListOf<Component>()
        val itemByHighestBid = getItemByHighestBid()

        bids.forEach { bid ->
            val item = itemByHighestBid.keys.find { it == bid.item } ?: return@forEach
            val highestBid = itemByHighestBid[item] ?: return@forEach

            if (bid == highestBid) {
                bid.user.sendMail(bid.user, item.itemStack)
                messages += "* ".component()
                    .append(item.itemStack.displayName().hoverEvent(item.itemStack.displayName().asHoverEvent()))
                    .append(": ${bid.user.name}".component())
                bid.user.player?.sendMessage("경매에서 ${item.itemStack.displayName()}을(를) 구매하셨습니다.")
            } else {
                bid.user.money += bid.amount
                bid.user.player?.sendMessage("경매 실패로 ${bid.amount}원이 반환되었습니다.")
            }
        }

        Bukkit.broadcast("* ---------".component())
        Bukkit.broadcast("* 경매 결과".component())
        messages.forEach(Bukkit::broadcast)
        Bukkit.broadcast("* 경매에 참여해주셔서 감사합니다.".component())
        Bukkit.broadcast("* ---------".component())

        clear()
    }
}

object AuctionItems : IntIdTable() {
    val auction = reference("auction", Auctions)
    val item = itemStack("item")
}

class AuctionItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuctionItem>(AuctionItems)

    var auction by Auction referencedOn AuctionItems.auction
    var itemStack by AuctionItems.item
}

object AuctionBids : IntIdTable() {
    val auction = reference("auction", Auctions)
    val item = reference("item", AuctionItems)
    val user = reference("user", Users)
    val amount = integer("amount")
}

class AuctionBid(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuctionBid>(AuctionBids)

    var auction by Auction referencedOn AuctionBids.auction
    var item by AuctionItem referencedOn AuctionBids.item
    var user by User referencedOn AuctionBids.user
    var amount by AuctionBids.amount
}

/**
 * auction setting [name] ui
 *
 * background
 * 2,3 ~ 2,7 empty -> auction items
 * 3,9 -> confirm
 */
class AuctionSettingUi(
    private val auction: Auction
) : CustomInventory("경매장 아이템 설정", 27) {
    init {
        inventory {
            background(BACKGROUND)

            empty(2 to 3, 2 to 7)

            auction.items.forEachIndexed { index, auctionItem ->
                item(2 to 3 + index, auctionItem.itemStack) {
                    it.isCancelled = false
                }
            }

            button(3 to 9, CONFIRM_ICON) {
                auction.addItems(getItems(2 to 3, 2 to 7).filterNotNull())
                player.closeInventory()
            }
        }
    }
}


/**
 * auction item list ui
 *
 * background
 * 2,3 2,5 2,7 4,4 4,6 -> items
 * 6,5 -> left times
 */
class AuctionItemListUi(
    private val auction: Auction
) : CustomInventory("경매장", 54) {
    init {
        inventory {
            loggedTransaction {
                background(BACKGROUND)

                val slots = listOf(2 to 3, 2 to 5, 2 to 7, 4 to 4, 4 to 6)
                auction.items.mapIndexed { index, item -> slots[index] to item }.forEach { (slot, item) ->
                    button(slot, item.itemStack.edit {
                        if (auction.type == AuctionType.BLIND) {
                            addLore("금액: ?")
                        } else {
                            addLore("금액: ${auction.getBids().maxByOrNull(AuctionBid::amount)?.amount ?: 0}")
                        }
                    }) {
                        if (auction.isOwner(player.user)) {
                            player.sendMessage("경매장 주인은 경매에 참여할 수 없습니다.")
                            return@button
                        }

                        if (auction.isExpired()) {
                            player.sendMessage("경매장이 종료되었습니다.")
                            player.closeInventory()
                            return@button
                        }

                        AnvilInventory(
                            title = "금액 입력",
                            text = "금액을 입력하세요.",
                            onClickResult = { state ->
                                loggedTransaction anvil@{
                                    val amount = state.text.toIntOrNull()
                                        ?: return@anvil listOf(ResponseAction.replaceInputText("숫자를 입력하세요."))

                                    // 같은 아이템에 입찰을 한 적이 있으면 그 입찰가 가져오기
                                    val bid = auction.getBids(player.user).find { it.item == item }

                                    // 돈 체크를 하는데 이전 입찰가 + 현재 보유 금액으로 계산함
                                    val previousAmount = bid?.amount ?: 0
                                    val totalAmount = previousAmount + player.user.money

                                    if (totalAmount < amount) {
                                        player.sendMessage("소지금이 부족합니다.")
                                        return@anvil listOf(ResponseAction.close())
                                    }

                                    if (bid != null) {
                                        bid.amount = amount
                                        player.user.money -= amount - previousAmount
                                    } else {
                                        auction.addBid(player.user, item, amount)
                                        player.user.money -= amount
                                    }

                                    player.closeInventory()
                                    player.sendMessage("* ----------------")
                                    player.sendMessage("* 경매 참여 내역")
                                    player.sendMessage(
                                        "* 상품: ".component()
                                            .append(
                                                item.itemStack.displayName()
                                                    .hoverEvent(item.itemStack.asHoverEvent(UnaryOperator.identity()))
                                            )
                                    )
                                    player.sendMessage("* 등록 금액: $amount")
                                    player.sendMessage("* 성공적으로 참여하였습니다.")
                                    player.sendMessage("* ----------------")
                                    return@anvil listOf(ResponseAction.close())
                                }
                            }
                        ).open(player)
                    }
                }

                val leftTime = auction.expirationTime.toInstant(ZoneOffset.UTC).toEpochMilli() - LocalDateTime.now()
                    .toInstant(ZoneOffset.UTC).toEpochMilli()

                display(6 to 9, icon {
                    name = "남은 시간".component()
                    lore = listOf("남은 시간: $leftTime".component())
                })
            }
        }

        onInventoryOpen {
//            runTaskRepeat(1000L) {
//                update()
//            }
        }
    }
}