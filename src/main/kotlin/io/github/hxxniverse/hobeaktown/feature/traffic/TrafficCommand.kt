package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.StringType
import io.github.monun.kommand.kommand
import io.github.monun.kommand.node.KommandNode
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class TrafficCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("교통") {
                then("버스역") {
                    then("추가", "name" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            createBusStation(it["name"])
                        }
                    }
                    then("삭제", "busStation" to busStation()) {
                        executes {
                            deleteBusStation(it["busStation"])
                        }
                    }
                    then("풍경", "busStation" to busStation()) {
                        executes {
                            setBusStationScenery(it["busStation"])
                        }
                    }
                    then("순서", "busStation" to busStation(), "order" to int()) {
                        executes {
                            setBusStationOrder(it["busStation"], it["order"])
                        }
                    }
                    then("티켓") {
                        then("생성", "amount" to int()) {
                            executes {
                                createBusTicket(it["amount"])
                            }
                        }
                        then("아이템설정") {
                            executes {
                                setBusTicket()
                            }
                        }
                    }
                }
                then("지하철역") {
                    then("추가", "name" to string()) {
                        executes {
                            createSubwayStation(it["name"])
                        }
                    }
                    then("삭제", "subwayStation" to subwayStation()) {
                        executes {
                            deleteSubwayStation(it["subwayStation"])
                        }
                    }
                    then("풍경", "subwayStation" to subwayStation()) {
                        executes {
                            setSubwayStationScenery(it["subwayStation"])
                        }
                    }
                    then("순서", "subwayStation" to subwayStation(), "order" to int()) {
                        executes {
                            setSubwayStationOrder(it["subwayStation"], it["order"])
                        }
                    }
                    then("티켓") {
                        then("생성", "amount" to int()) {
                            executes {
                                createSubwayTicket(it["amount"])
                            }
                        }
                        then("아이템설정") {
                            executes {
                                setSubwayTicket()
                            }
                        }
                    }
                }
                then("비행기") {
                    then("출발", "airport" to airplane()) {
                        executes {
                            // TODO
                        }
                    }
                    then("복귀", "airport" to airplane()) {
                        executes {
                            // TODO
                        }
                    }
                    then("풍경", "airport" to airplane()) {
                        executes {
                            // TODO
                        }
                    }
                    then("티켓") {
                        then("생성", "amount" to int()) {
                            executes {
                                createAirplaneTicket(it["amount"])
                            }
                        }
                        then("아이템설정") {
                            executes {
                                setAirplaneTicket()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun KommandNode.busStation(): KommandArgument<BusStation> =
        dynamic(type = StringType.GREEDY_PHRASE) { _, input ->
            val busStation = transaction {
                BusStation.find { BusStations.name eq input }.firstOrNull()
            }
            return@dynamic busStation
        }.apply {
            suggests {
                suggest(transaction {
                    BusStation.all().map { it.name }
                })
            }
        }

    private fun KommandNode.subwayStation(): KommandArgument<SubwayStation> =
        dynamic(type = StringType.GREEDY_PHRASE) { context, input ->
            val subwayStation = transaction {
                SubwayStation.find { SubwayStations.name eq input }.firstOrNull()
            }
            return@dynamic subwayStation
        }.apply {
            suggests {
                suggest(transaction {
                    SubwayStation.all().map { it.name }
                })
            }
        }

    private fun KommandNode.airplane(): KommandArgument<Airplane> =
        dynamic(type = StringType.GREEDY_PHRASE) { context, input ->
            val airplane = transaction {
                Airplane.find { Airplanes.airportName eq input }.firstOrNull()
            }
            return@dynamic airplane
        }.apply {
            suggests {
                suggest(transaction {
                    Airplane.all().map { it.airportName }
                })
            }
        }
}

private fun KommandSource.createBusTicket(amount: Int) {
    TrafficConfig.configData.busTicket.clone()
        .apply { this.amount = amount }
        .run { player.inventory.addItem(this) }
    player.sendInfoMessage("버스 티켓 $amount 개를 생성하였습니다.")
}

private fun KommandSource.createSubwayTicket(amount: Int) {
    TrafficConfig.configData.subwayTicket.clone()
        .apply { this.amount = amount }
        .run { player.inventory.addItem(this) }
    player.sendInfoMessage("지하철 티켓 $amount 개를 생성하였습니다.")
}

private fun KommandSource.createAirplaneTicket(amount: Int) {
    TrafficConfig.configData.airplaneTicket.clone()
        .apply { this.amount = amount }
        .run { player.inventory.addItem(this) }
    player.sendInfoMessage("비행기 티켓 $amount 개를 생성하였습니다.")
}

private fun KommandSource.setBusTicket() {
    if (player.inventory.itemInMainHand.type.isAir) {
        sender.sendErrorMessage("손에 아이템을 들고 명령어를 입력해주세요.")
        return
    }
    TrafficConfig.updateConfigData {
        copy(busTicket = player.inventory.itemInMainHand)
    }
    player.sendInfoMessage("버스 티켓 아이템을 설정하였습니다.")
}

private fun KommandSource.setSubwayTicket() {
    if (player.inventory.itemInMainHand.type.isAir) {
        sender.sendErrorMessage("손에 아이템을 들고 명령어를 입력해주세요.")
        return
    }
    TrafficConfig.updateConfigData {
        copy(subwayTicket = player.inventory.itemInMainHand)
    }
    player.sendInfoMessage("지하철 티켓 아이템을 설정하였습니다.")
}

private fun KommandSource.setAirplaneTicket() {
    if (player.inventory.itemInMainHand.type.isAir) {
        sender.sendErrorMessage("손에 아이템을 들고 명령어를 입력해주세요.")
        return
    }
    TrafficConfig.updateConfigData {
        copy(airplaneTicket = player.inventory.itemInMainHand)
    }
    player.sendInfoMessage("비행기 티켓 아이템을 설정하였습니다.")
}

private fun KommandSource.setBusStationOrder(busStation: BusStation, order: Int) {
    val stations = BusStation.all().sortedBy { it.order }
    var shift = false

    stations.forEach {
        if (shift) {
            it.order = (it.order ?: 0) + 1
        }
        if (it.name == busStation.name) {
            it.order = order
            shift = true
        } else if ((it.order ?: 0) >= order) {
            it.order = (it.order ?: 0) + 1
        }
    }

    sender.sendInfoMessage("버스역 ${busStation.name} 순서 설정 완료")
}

private fun KommandSource.setBusStationScenery(busStation: BusStation) {
    transaction {
        busStation.scenery = location
    }

    sender.sendInfoMessage("버스역 ${busStation.name} 풍경 설정 완료")
}

private fun KommandSource.deleteBusStation(busStation: BusStation) {
    transaction {
        busStation.delete()
    }

    sender.sendInfoMessage("버스역 ${busStation.name} 삭제 완료")
}

private fun KommandSource.createBusStation(name: String) {
    transaction {
        val order = BusStation.all().count().toInt()
        BusStation.new {
            this.name = name
            this.position = null
            this.scenery = null
            this.order = order
        }
    }

    sender.sendInfoMessage("버스역 $name 추가 완료")
}

private fun KommandSource.setSubwayStationOrder(busStation: SubwayStation, order: Int) {
    val stations = SubwayStation.all().sortedBy { it.order }
    var shift = false

    stations.forEach {
        if (shift) {
            it.order = (it.order ?: 0) + 1
        }
        if (it.name == busStation.name) {
            it.order = order
            shift = true
        } else if ((it.order ?: 0) >= order) {
            it.order = (it.order ?: 0) + 1
        }
    }

    sender.sendInfoMessage("지하철역 ${busStation.name} 순서 설정 완료")
}

private fun KommandSource.setSubwayStationScenery(busStation: SubwayStation) {
    transaction {
        busStation.scenery = location
    }

    sender.sendInfoMessage("지하철역 ${busStation.name} 풍경 설정 완료")
}

private fun KommandSource.deleteSubwayStation(busStation: SubwayStation) {
    transaction {
        busStation.delete()
    }

    sender.sendInfoMessage("지하철역 ${busStation.name} 삭제 완료")
}

private fun KommandSource.createSubwayStation(name: String) {
    transaction {
        SubwayStation.new {
            this.name = name
            this.position = null
            this.scenery = null
            this.order = id.value
        }
    }

    sender.sendInfoMessage("지하철역 $name 추가 완료")
}
