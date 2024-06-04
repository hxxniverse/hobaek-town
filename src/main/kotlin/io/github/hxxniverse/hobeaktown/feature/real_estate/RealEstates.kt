package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.economy.util.hasMoney
import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.util.database.location
import org.bukkit.Bukkit
import org.bukkit.Location
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
    val owner = uuid("owner")
    val pos1 = location("pos1")
    val pos2 = location("pos2")
    val expirationDate = datetime("expiration_date")
    val signLocation = location("sign_location").nullable()
    val grade = enumeration("grade", RealEstateGrade::class).nullable()
}

class RealEstate(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<RealEstate>(RealEstates) {
        fun create(
            name: String,
            price: Int,
            due: Int,
            type: RealEstateType,
            owner: UUID,
            pos1: Location,
            pos2: Location,
        ) = transaction {
            new {
                this.name = name
                this.price = price
                this.due = due
                this.type = type
                this.owner = owner
                this.pos1 = pos1
                this.pos2 = pos2
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
    var expirationDate by RealEstates.expirationDate
    var signLocation by RealEstates.signLocation

    fun showInfo(player: Player) {
        player.sendMessage("부동산 정보")
        player.sendMessage("이름 : $name")
        player.sendMessage("가격 : $price")
        player.sendMessage("기간 : $due")
        player.sendMessage("소유자 : ${Bukkit.getOfflinePlayer(owner).name}")
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
            RealEstateMembers.realEstate eq id and (RealEstateMembers.member eq player.uniqueId) and (RealEstateMembers.expirationDate greaterEq LocalDateTime.now())
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
        expirationDate = LocalDateTime.now().plusDays(due.toLong())
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

    fun setGrade(grade: RealEstateGrade) {
        this.grade = grade
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
            RealEstateMembers.realEstate eq id and (RealEstateMembers.member eq target.uniqueId) and (RealEstateMembers.expirationDate greaterEq LocalDateTime.now())
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

        expirationDate = expirationDate.plusDays(due.toLong())
        player.sendMessage("토지 기간을 연장하였습니다.")
    }

    // 대여는 멤버 추가하고 상태를 RENT 로 변경
    fun rent(player: Player, target: Player) {
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
    val yLimit: Int
) {
    C(5),
    B(10),
    A(14),
    S(25),
    R(40)
}
