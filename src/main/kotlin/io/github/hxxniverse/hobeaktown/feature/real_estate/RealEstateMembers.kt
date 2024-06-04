package io.github.hxxniverse.hobeaktown.feature.real_estate

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object RealEstateMembers : IntIdTable() {
    val realEstate = reference("real_estate", RealEstates)
    val member = uuid("member")
    val expirationDate = datetime("expiration_date")
}

class RealEstateMember(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RealEstateMember>(RealEstateMembers) {
        fun create(
            realEstate: RealEstate,
            member: java.util.UUID,
            expirationDate: java.time.LocalDateTime
        ) = new {
            this.realEstate = realEstate
            this.member = member
            this.expirationDate = expirationDate
        }
    }

    var realEstate by RealEstate referencedOn RealEstateMembers.realEstate
    var member by RealEstateMembers.member
    var expirationDate by RealEstateMembers.expirationDate
}