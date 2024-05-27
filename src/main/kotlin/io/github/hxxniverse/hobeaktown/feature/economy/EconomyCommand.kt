package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.entity.Atm
import io.github.hxxniverse.hobeaktown.feature.economy.ui.*
import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat


class EconomyCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("atm") {
                requires { sender.isOp }
                then("help") {
                    executes {
                        text(
                            """
                         * "명령어 리스트 ( Command list )
                         * 모든 명령어는 OP만 사용가능합니다.
                         *
                         * /atm remittance
                         * 명령어 입력시 GUI가 출력됩니다.
                         *
                         * /atm deposit
                         * 명령어 입력시 GUI가 출력됩니다.
                         *
                         * /atm withdraw
                         * 명령어 입력시 GUI가 출력됩니다.
                         *
                         * /atm check (플레이어)
                         * 플레이어가 소지하고있는 금액및 정보를 확인 할 수있습니다.
                         *
                         * /atm block
                         * 바라보는 블럭을 ATM 으로 설정합니다.
                         * 이미 등록되어 있는 블럭 일 경우 제거됩니다.
                         *
                         * /atm set (플레이어) (금액)
                         * 플레이어의 ATM 금액을 변경합니다.
                         *
                         * /atm give (플레이어) (금액)
                         * 플레이어의 ATM 금액에서 지정한 금액을 추가합니다.
                         *
                         * /atm take (플레이어) (금액)
                         * 플레이어의 ATM 금액에서 지정한 금액을 차감합니다.""".trimIndent()
                        ).also(sender::sendMessage)
                    }
                }
                then("open") {
                    requires { sender is Player }
                    executes {
                        AtmMenuUi().open(player)
                    }
                }
                then("remittance") {
                    requires { sender is Player }
                    executes {
                        AtmRemittanceRecipientUi().open(player)
                    }
                }
                then("deposit") {
                    requires { sender is Player }
                    executes {
                        AtmDepositUi().open(player)
                    }
                }
                then("withdraw") {
                    requires { sender is Player }
                    executes {
                        AtmWithdrawUi().open(player)
                    }
                }
                then("check") {
                    then("player" to player()) {
                        executes {
                            val player: Player by it

                            text(player.name)
                                .append(text("님의 금액: "))
                                .append(text(DecimalFormat("#,##0").format(player.money)))
                                .also(sender::sendMessage)
                        }
                    }
                }
                then("block") {
                    requires { sender is Player }
                    executes {
                        val block = player.getTargetBlock(null, 5)

                        if (block.type == Material.AIR) {
                            text("블록을 찾을 수 없습니다.")
                                .also(sender::sendMessage)
                            return@executes
                        }

                        transaction {
                            val atm = Atm.findByLocation(block.location)

                            if (atm != null) {
                                atm.delete()
                                text("ATM 블록에서 제거되었습니다.")
                                    .also(sender::sendMessage)
                                return@transaction
                            } else {
                                Atm.create(block.location)
                                text("ATM 블록으로 설정되었습니다.")
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
                then("set") {
                    then("player" to player()) {
                        then("money" to int()) {
                            executes {
                                val player: Player by it
                                val money: Int by it

                                player.money = money

                                text(player.name)
                                    .append(text("님의 금액을 "))
                                    .append(text(DecimalFormat("#,##0").format(money)))
                                    .append(text("로 변경되었습니다."))
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
                then("give") {
                    then("player" to player()) {
                        then("money" to int()) {
                            executes {
                                val player: Player by it
                                val money: Int by it

                                player.money += money

                                text(player.name)
                                    .append(text("님의 금액을 "))
                                    .append(text(DecimalFormat("#,##0").format(money)))
                                    .append(text("만큼 증가시켰습니다."))
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
                then("take") {
                    then("player" to player()) {
                        then("money" to int()) {
                            executes {
                                val player: Player by it
                                val money: Int by it

                                player.money -= money

                                text(player.name)
                                    .append(text("님의 금액을 "))
                                    .append(text(DecimalFormat("#,##0").format(money)))
                                    .append(text("만큼 차감시켰습니다."))
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
            }
        }
    }
}