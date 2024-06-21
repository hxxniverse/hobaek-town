package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.economy.util.hasMoney
import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstateMembers.expirationDate
import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.extension.getBlockList
import io.github.hxxniverse.hobeaktown.util.extension.text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Sign
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

val armorStands = mutableListOf<Entity>()

object RealEstates : IntIdTable() {
    val name = varchar("name", 255)
    val price = integer("price")
    val due = integer("due")
    val type = enumeration("type", RealEstateType::class)
    val owner = uuid("owner").nullable()
    val pos1 = location("pos1")
    val pos2 = location("pos2")
    val rentStartDate = datetime("rent_start_date").nullable()
    val rentEndDate = datetime("rent_end_date").nullable()
    val signLocation = location("sign_location")
    val grade = enumeration("grade", RealEstateGrade::class).nullable()
}

class RealEstate(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<RealEstate>(RealEstates) {
        fun create(
            name: String,
            price: Int,
            due: Int,
            type: RealEstateType,
            pos1: Location,
            pos2: Location,
            signLocation: Location,
        ) = transaction {
            new {
                this.name = name
                this.price = price
                this.due = due
                this.type = type
                this.pos1 = Location(pos1.world, min(pos1.x, pos2.x), min(pos1.y, pos2.y), min(pos1.z, pos2.z))
                this.pos2 = Location(pos2.world, max(pos1.x, pos2.x), max(pos1.y, pos2.y), max(pos1.z, pos2.z))
                this.signLocation = signLocation
            }
        }

        fun list(player: Player) {
            player.sendMessage("부동산 목록")
            transaction {
                RealEstate.find { RealEstates.owner eq player.uniqueId }.forEach {
                    player.sendMessage("${it.name} ${it.pos1} ${it.due}일")
                }
            }
        }
    }

    var name by RealEstates.name
    var price by RealEstates.price
    var due by RealEstates.due
    var type by RealEstates.type
    var owner by RealEstates.owner
    var pos1 by RealEstates.pos1
    var pos2 by RealEstates.pos2
    var grade by RealEstates.grade
    var rentStartDate by RealEstates.rentStartDate
    var rentEndDate by RealEstates.rentEndDate
    var signLocation by RealEstates.signLocation

    val centerLocation: Location
        get() = Location(pos1.world, (pos1.x + pos2.x) / 2, (pos1.y + pos2.y) / 2, (pos1.z + pos2.z) / 2)

    val remainingDays: Int
        get() = rentEndDate?.dayOfYear?.minus(LocalDateTime.now().dayOfYear) ?: 0

    override fun toString(): String {
        return "RealEstate(name='$name', price=$price, due=$due, type=$type, owner=$owner, pos1=$pos1, pos2=$pos2, rentStartDate=$rentStartDate, rentEndDate=$rentEndDate, signLocation=$signLocation, grade=$grade)"
    }

    fun updateSign() = transaction {
        val line1 = name
        val line2 = "가격 : $price"
        // 만약 rentStartDate 가 null 이라면 due 값을 그게 아니라면 rentStartDate ~ rentEndDate 값을 보여줌 포맷은 MM.dd
        val line3 =
            if (rentStartDate == null) "기간 : $due" else "기간 : ${rentStartDate?.monthValue}.${rentStartDate?.dayOfMonth} ~ ${rentEndDate?.monthValue}.${rentEndDate?.dayOfMonth}"
        // 만약 owner 가 null 이라면 판매중 아니면 해당 유저의 이름
        val line4 = owner?.let { Bukkit.getOfflinePlayer(it).name } ?: "판매중"

        // signLocation 에서 블록을 가져와서 Sign 이라면 해당 라인에 업데이트
        (signLocation.block.state as Sign).let {
            it.setLine(0, line1)
            it.setLine(1, line2)
            it.setLine(2, line3)
            it.setLine(3, line4)
            it.update(true)
            println(it)
        }

        armorStands.filter {
            it.x == signLocation.clone().add(0.5, 0.0, 0.5).x && it.z == signLocation.clone().add(0.5, 0.0, 0.5).z
        }.forEach {
            it.passengers.forEach { passenger -> it.removePassenger(passenger) }
            it.remove()
        }.also {
            armorStands.removeAll {
                it.x == signLocation.clone().add(0.5, 0.0, 0.5).x && it.z == signLocation.clone().add(0.5, 0.0, 0.5).z
            }
        }

        signLocation.world.spawn(signLocation.clone().add(0.5, 0.0, 0.5), ArmorStand::class.java).apply {
            isVisible = false
            isCustomNameVisible = true
            customName = if (owner == null) "판매중" else "소유자: ${Bukkit.getOfflinePlayer(owner!!).name}"
            setGravity(false)
        }.also { armorStands.add(it) }

        if (type == RealEstateType.LAND) {
            signLocation.world.spawn(signLocation.clone().add(0.5, -0.3, 0.5), ArmorStand::class.java).apply {
                isVisible = false
                isCustomNameVisible = true
                customName = "토지등급: $grade"
                setGravity(false)
            }.also { armorStands.add(it) }
        }

        // 여기서 만약 owner 가 null이 아니라면 Item 엔티티, PLAYER_SKULL 이고 owner 값은 owner 소환하고 armorStands 에 추가
        if (owner != null) {
            val headStand =
                signLocation.world.spawn(signLocation.clone().add(0.5, 1.0, 0.5), ArmorStand::class.java).apply {
                    isVisible = false
                    isCustomNameVisible = false
                    setGravity(false)
                    customName(text(""))
                }.also { armorStands.add(it) }

            signLocation.world.spawn(signLocation, Item::class.java).apply {
                setGravity(false)
                setCanPlayerPickup(false)
                setCanMobPickup(false)
                itemStack = ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = (itemMeta as SkullMeta).apply {
                        owningPlayer = Bukkit.getOfflinePlayer(this@RealEstate.owner!!)
                    }
                }
            }.also { armorStands.add(it) }
                .also { headStand.addPassenger(it) }
        }
    }

    // 해당 영토에 있는지 체크
    fun isInside(location: Location): Boolean = transaction {
        if (type == RealEstateType.LAND) {
            return@transaction location.x in pos1.x..pos2.x && location.z in pos1.z..pos2.z
        }

        return@transaction location.x in pos1.x..pos2.x && location.y in pos1.y..pos2.y && location.z in pos1.z..pos2.z
    }

    fun isOwner(player: OfflinePlayer): Boolean = transaction {
        return@transaction player.uniqueId == owner
    }

    fun isMember(player: Player): Boolean = transaction {
        return@transaction RealEstateMember.find {
            RealEstateMembers.realEstate eq this@RealEstate.id and (RealEstateMembers.member eq player.uniqueId) and (expirationDate greaterEq LocalDateTime.now())
        }.firstOrNull() != null
    }

    fun buy(player: Player) = transaction {
        if (isOwner(player)) {
            player.sendMessage("이미 소유하고 있는 토지입니다.")
            return@transaction
        }

        if (player.hasMoney(price).not()) {
            player.sendMessage("돈이 부족합니다.")
            return@transaction
        }

        owner = player.uniqueId
        rentStartDate = LocalDateTime.now()
        rentEndDate = rentStartDate?.plusDays(due.toLong())
        val certificate = RealEstatesItem.REAL_ESTATE_CERTIFICATE(this@RealEstate)
        player.inventory.addItem(certificate)
        player.sendMessage("토지를 구매하였습니다.")
        return@transaction
    }

    fun sell(player: Player) = transaction {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return@transaction
        }

        player.money += price
        player.sendMessage("토지를 판매하였습니다.")
    }

    fun transfer(player: Player, target: OfflinePlayer) = transaction {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return@transaction
        }

        if (isOwner(target)) {
            player.sendMessage("이미 소유하고 있는 토지입니다.")
            return@transaction
        }

        owner = target.uniqueId
        player.sendMessage("토지를 양도하였습니다.")
    }

    fun clean() = transaction {
        val initBlocks = getScheme()
        val currentBlocks = (pos1 to pos2).getBlockList()

        val diff = initBlocks.filter { block -> currentBlocks.contains(block).not() }
        // TODO diff 블록들을 뭔가 줘야 하는데 상자 같은건 어떻게 줄지..?
        diff.forEach { block -> block.type = Material.AIR }
    }

    fun invite(player: Player, target: Player) = transaction {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return@transaction
        }

        // 이미 초대되고, 권한이 만기가 안되어 있다면
        if (isMember(target)) {
            player.sendMessage("이미 초대한 플레이어입니다.")
            return@transaction
        }

        RealEstateMember.create(this@RealEstate, target.uniqueId, LocalDateTime.now().withYear(2500))
        player.sendMessage("플레이어를 초대하였습니다.")
        target.sendMessage("토지에 초대되었습니다.")
    }

    fun kick(player: Player, target: Player) = transaction {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return@transaction
        }

        val member = RealEstateMember.find {
            (RealEstateMembers.realEstate eq this@RealEstate.id) and (RealEstateMembers.member eq target.uniqueId) and (expirationDate greaterEq LocalDateTime.now())
        }.firstOrNull()

        if (member == null) {
            player.sendMessage("초대되지 않은 플레이어입니다.")
            return@transaction
        }

        member.delete()
        player.sendMessage("플레이어를 추방하였습니다.")
        target.sendMessage("토지에서 추방되었습니다.")
    }

    fun listMembers(player: Player) = transaction {
        player.sendMessage("토지 멤버 목록")
        RealEstateMember.find { RealEstateMembers.realEstate eq this@RealEstate.id }.forEach {
            player.sendMessage("${Bukkit.getOfflinePlayer(it.member).name} ${it.expirationDate}")
        }
    }

    fun extend(player: Player) = transaction {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return@transaction
        }

        rentEndDate = rentEndDate?.plusDays(due.toLong())
        player.sendMessage("토지 기간을 연장하였습니다.")
    }

    // 대여는 멤버 추가하고 상태를 RENT 로 변경
    fun rent(player: Player, target: Player, price: Int) = transaction {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return@transaction
        }

        if (isOwner(target)) {
            player.sendMessage("이미 소유하고 있는 토지입니다.")
            return@transaction
        }

        RealEstateMember.create(this@RealEstate, target.uniqueId, LocalDateTime.now().plusDays(due.toLong()))
        player.sendMessage("토지를 대여하였습니다.")
    }
}

enum class RealEstateType {
    NORMAL, LAND
}

enum class RealStateStatus {
    NORMAL, RENT, SALE
}

enum class RealEstateGrade(
    val yLimit: Int,
) {
    C(5), B(10), A(14), S(25), R(40)
}
