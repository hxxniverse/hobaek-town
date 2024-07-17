package io.github.hxxniverse.hobeaktown.feature.wasteland;

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class WasteLandListener implements Listener {
    private final WasteLandFeature feature = WasteLandFeature.getInstance();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND || e.getClickedBlock() == null) {
            return;
        }

        Block block = e.getClickedBlock();
        if(block.getType() != Material.SAND || block.getType() != Material.GRAVEL) {
            return;
        }

        if(e.getItem() == null || e.getItem().getType() != Material.BRUSH || feature.isConstructionSite(e.getPlayer().getLocation())) {
            return;
        }

        e.getPlayer().sendMessage("§6[황무지]§7 아이템을 찾았습니다.");
        // e.getPlayer().getInventory().addItem(feature.getRandomItem(??));
        // TODO 데이터 i/o 학습 후 -> 솔 관련 처리

        // 베드락으로 변환 후 3분 뒤 솔질이 가능한 블럭으로 변경
        Map<Location, Material> map = WasteLandFeature.getInstance().getBrushedBlocks();
        map.put(block.getLocation(), block.getType());
        block.setType(Material.BEDROCK);
        Bukkit.getScheduler().runTaskLater(HobeakTownPlugin.plugin, () -> block.setType(map.get(block.getLocation())), 3 * 60 * 20);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null || !(e.getClickedInventory() instanceof WasteLandGui)) {
            return;
        }

        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.GLASS_PANE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!(e.getInventory() instanceof WasteLandGui)) {
            return;
        }

        Inventory inventory = ((WasteLandGui) e.getInventory()).getInventory();

        for(int i=9; i<=17; i++) {
            if(inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                feature.saveGuiItem(i, inventory.getItem(i));
            }
        }

        for(int i=36; i<=44; i++) {
            if(inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                feature.saveGuiItem(i, inventory.getItem(i));
            }
        }
    }
}
