package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.database.location
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Define BusStation table
object BusStations : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val position = location("position").nullable() // pos1 or pos2
    val scenery = location("scenery").nullable()
    val order = integer("order").nullable()
}

// Define SubwayStation table
object SubwayStations : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val position = location("position").nullable() // pos1 or pos2
    val scenery = location("scenery").nullable()
    val order = integer("order").nullable()
}

// Define Airplane table
object Airplanes : IntIdTable() {
    val airportName = varchar("airport_name", 255).uniqueIndex()
    val departurePosition = location("departure_position").nullable() // pos1 or pos2
    val returnPosition = location("return_position").nullable() // pos1 or pos2
    val scenery = location("scenery").nullable()
}

// DAO for BusStation
class BusStation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BusStation>(BusStations)

    var name by BusStations.name
    var position by BusStations.position
    var scenery by BusStations.scenery
    var order by BusStations.order
}

// DAO for SubwayStation
class SubwayStation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SubwayStation>(SubwayStations)

    var name by SubwayStations.name
    var position by SubwayStations.position
    var scenery by SubwayStations.scenery
    var order by SubwayStations.order
}

// DAO for Airplane
class Airplane(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Airplane>(Airplanes)

    var airportName by Airplanes.airportName
    var departurePosition by Airplanes.departurePosition
    var returnPosition by Airplanes.returnPosition
    var scenery by Airplanes.scenery
}

// Function to execute commands

