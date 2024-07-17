package wasteland

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WasteLandCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("황무지") {
                requires { sender is Player && sender.isOp }
                then("아이템설정") {
                    executes {
                        player.openInventory(WasteLandFeature.getInstance().gui);
                    }
                }
                then("솔") {
                    then("등급설정") {
                        then("level" to int()) {
                            executes {
                                if(player.inventory.itemInMainHand.type != Material.BRUSH) {
                                    player.sendMessage("§6[황무지]§7 솔 등급설정은 솔을 들고있는 상태에서만 사용 가능합니다.");
                                    return@executes;
                                }

                                val level : Int by it
                                WasteLandFeature.getInstance().setBrushLevel(player.inventory.itemInMainHand, level);
                                player.sendMessage("§6[황무지]§7 손에 들고있는 솔의 등급을§f " + level + "레벨§7로 변경하였습니다.");
                            }
                        }
                    }
                }
            }
        }
    }
}