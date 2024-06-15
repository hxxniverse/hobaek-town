package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.economy.util.hasMoney
import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstateMembers.expirationDate
import io.github.hxxniverse.hobeaktown.util.database.location
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.craftbukkit.v1_20_R1.block.CraftSign
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

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
                this.pos1 = pos1
                this.pos2 = pos2
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

    override fun toString(): String {
        return "RealEstate(name='$name', price=$price, due=$due, type=$type, owner=$owner, pos1=$pos1, pos2=$pos2, rentStartDate=$rentStartDate, rentEndDate=$rentEndDate, signLocation=$signLocation, grade=$grade)"
    }

    fun updateSign() {
        val line1 = name
        val line2 = "가격 : $price"
        // 만약 rentStartDate 가 null 이라면 due 값을 그게 아니라면 rentStartDate ~ rentEndDate 값을 보여줌 포맷은 MM.dd
        val line3 = if (rentStartDate == null) "기간 : $due" else "기간 : ${rentStartDate?.monthValue}.${rentStartDate?.dayOfMonth} ~ ${rentEndDate?.monthValue}.${rentEndDate?.dayOfMonth}"
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
    }

    fun showInfo(player: Player) {
        player.sendMessage("부동산 정보")
        player.sendMessage("이름 : $name")
        player.sendMessage("가격 : $price")
        player.sendMessage("기간 : $due")
        player.sendMessage("소유자 : ${owner?.let { Bukkit.getOfflinePlayer(it).name } ?: "판매중"}")
    }

    // 해당 영토에 있는지 체크
    fun isInside(location: Location): Boolean {
        val x1 = pos1.x
        val x2 = pos2.x
        val y1 = pos1.y
        val y2 = pos2.y
        val z1 = pos1.z
        val z2 = pos2.z

        return location.x in x1..x2 && location.y in y1..y2 && location.z in z1..z2
    }

    fun isOwner(player: Player): Boolean {
        return player.uniqueId == owner
    }

    fun isMember(player: Player): Boolean {
        return RealEstateMember.find {
            RealEstateMembers.realEstate eq id and (RealEstateMembers.member eq player.uniqueId) and (expirationDate greaterEq LocalDateTime.now())
        }.firstOrNull() != null
    }

    fun buy(player: Player) {
        if (isOwner(player)) {
            player.sendMessage("이미 소유하고 있는 토지입니다.")
            return
        }

        if (player.hasMoney(price).not()) {
            player.sendMessage("돈이 부족합니다.")
            return
        }

        owner = player.uniqueId
        rentStartDate = LocalDateTime.now()
        rentEndDate = rentStartDate?.plusDays(due.toLong())
        player.sendMessage("토지를 구매하였습니다.")
    }

    fun sell(player: Player) {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return
        }

        player.money += price
        player.sendMessage("토지를 판매하였습니다.")
    }

    fun transfer(player: Player, target: Player) {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return
        }

        if (isOwner(target)) {
            player.sendMessage("이미 소유하고 있는 토지입니다.")
            return
        }

        owner = target.uniqueId
        player.sendMessage("토지를 양도하였습니다.")
    }

    fun clean() {
        // TODO
    }

    fun invite(player: Player, target: Player) {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return
        }

        // 이미 초대되고, 권한이 만기가 안되어 있다면
        if (isMember(target)) {
            player.sendMessage("이미 초대한 플레이어입니다.")
            return
        }

        RealEstateMember.create(this, target.uniqueId, LocalDateTime.now().withYear(2500))
        player.sendMessage("플레이어를 초대하였습니다.")
        target.sendMessage("토지에 초대되었습니다.")
    }

    fun kick(player: Player, target: Player) {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return
        }

        val member = RealEstateMember.find {
            RealEstateMembers.realEstate eq id and (RealEstateMembers.member eq target.uniqueId) and (expirationDate greaterEq LocalDateTime.now())
        }.firstOrNull()

        if (member == null) {
            player.sendMessage("초대되지 않은 플레이어입니다.")
            return
        }

        member.delete()
        player.sendMessage("플레이어를 추방하였습니다.")
        target.sendMessage("토지에서 추방되었습니다.")
    }

    fun listMembers(player: Player) {
        player.sendMessage("토지 멤버 목록")
        RealEstateMember.find { RealEstateMembers.realEstate eq id }.forEach {
            player.sendMessage("${Bukkit.getOfflinePlayer(it.member).name} ${it.expirationDate}")
        }
    }

    fun extend(player: Player) {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return
        }

        rentEndDate = rentEndDate?.plusDays(due.toLong())
        player.sendMessage("토지 기간을 연장하였습니다.")
    }

    // 대여는 멤버 추가하고 상태를 RENT 로 변경
    fun rent(player: Player, target: Player, price: Int) {
        if (isOwner(player).not()) {
            player.sendMessage("소유하고 있지 않은 토지입니다.")
            return
        }

        if (isOwner(target)) {
            player.sendMessage("이미 소유하고 있는 토지입니다.")
            return
        }

        RealEstateMember.create(this, target.uniqueId, LocalDateTime.now().plusDays(due.toLong()))
        player.sendMessage("토지를 대여하였습니다.")
    }
}

enum class RealEstateType {
    NORMAL,
    LAND
}

enum class RealStateStatus {
    NORMAL, RENT, SALE
}

enum class RealEstateGrade(
    val yLimit: Int,
) {
    C(5),
    B(10),
    A(14),
    S(25),
    R(40)
}
