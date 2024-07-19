package wasteland;

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin;
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WasteLandListener implements Listener {
    private final WasteLandFeature feature = WasteLandFeature.INSTANCE;

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND || e.getClickedBlock() == null) {
            return;
        }

        Block block = e.getClickedBlock();
        if(block.getType() != Material.SAND && block.getType() != Material.GRAVEL) {
            return;
        }

        if(e.getItem() == null || e.getItem().getType() != Material.BRUSH) {
            return;
        }

        // 솔에 부여된 레벨이 없는 경우 처리
        if(feature.getBrushLevel(e.getItem()) == -1) {
            e.getPlayer().sendMessage("§6[황무지]§7 해당 아이템은 레벨이 부여된 솔이 아니므로 사용할 수 없습니다.");
            return;
        }

        e.setCancelled(true);

        // 아이템을 획득할 수 있는 블럭이 아닌 경우
        if(feature.getIdByLocation(e.getClickedBlock().getLocation()) == null) {
            return;
        }

        // 식별자 기반 블럭 -> 보상 아이템 조회
        Map<Integer, ItemStack> rewardMap = feature.getRewardsById(feature.getIdByLocation(e.getClickedBlock().getLocation()));

        e.getPlayer().getInventory().addItem(feature.getRandomItem(rewardMap, feature.getBrushLevel(e.getItem())));
        e.getPlayer().sendMessage("§6[황무지]§7 아이템을 찾았습니다.");

        // 베드락으로 변환 후 3분 뒤 솔질이 가능한 블럭으로 변경
        Map<Location, Material> map = WasteLandFeature.INSTANCE.getBrushedBlocks();
        map.put(block.getLocation(), block.getType());
        block.setType(Material.BEDROCK);
        Bukkit.getScheduler().runTaskLater(HobeakTownPlugin.plugin, () -> {
            block.setType(map.get(block.getLocation()));
            map.remove(block.getLocation());
        }, 3 * 60 * 20);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getItemInHand().getType() != Material.SAND && e.getItemInHand().getType() != Material.GRAVEL) {
            return;
        }

        if(e.getItemInHand().getItemMeta() == null || !e.getItemInHand().getItemMeta().hasDisplayName()) {
            return;
        }

        @SuppressWarnings("deprecation")
        String id = ChatColor.stripColor(e.getItemInHand().getItemMeta().getDisplayName());

        // 보상이 설정되어 있지 않은 일반 블럭인 경우
        if(feature.getRewardsById(id) == null) {
            return;
        }

        feature.addIdToLocation(e.getBlock().getLocation(), id);
        e.getPlayer().sendMessage("§6[황무지]§7 해당 블럭을 보상을 얻을 수 있는 블럭으로 설정했습니다.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() != Material.SAND && e.getBlock().getType() != Material.GRAVEL) {
            return;
        }

        if(feature.getIdByLocation(e.getBlock().getLocation()) == null) {
            return;
        }

        feature.deleteIdFromLocation(e.getBlock().getLocation());
        e.getPlayer().sendMessage("§6[황무지]§7 보상 획득이 가능한 블럭을 파괴하였습니다.");
    }

    @EventHandler
    public void onBrushGUIClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null || !(e.getClickedInventory() instanceof WasteLandBrushGUI)) {
            return;
        }

        e.setCancelled(true);

        if(e.getClick() == ClickType.LEFT) {
            ItemStack item = e.getCurrentItem();

            if(item != null && item.getType() != Material.AIR) {
                e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
            }

            return;
        }

        if(e.getClick() == ClickType.SHIFT_RIGHT) {
            ItemStack item = e.getCurrentItem();

            if(item != null && item.getType() != Material.AIR) {
                WasteLandFeature.INSTANCE.deleteBrush(item);
                ((WasteLandBrushGUI) e.getClickedInventory()).reloadGUI();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null || !(e.getClickedInventory() instanceof WasteLandGUI)) {
            return;
        }

        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.GLASS_PANE) {
            e.setCancelled(true);
            return;
        }

        if(e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.SAND || e.getCurrentItem().getType() == Material.GRAVEL)) {
            e.setCancelled(true);

            Map<Integer, ItemStack> rewardMap = new HashMap<>();
            Inventory inv = e.getClickedInventory();
            ItemStack item;

            // GUI 에서 rewardMap 으로 보상 정보 저장
            for(int i=9 ; i<=17 ; i++) {
                item = inv.getItem(i);

                if(item == null || item.getType() == Material.AIR) {
                    continue;
                }

                rewardMap.put(i, inv.getItem(i));
            }
            for(int i=36 ; i<=44 ; i++) {
                item = inv.getItem(i);

                if(item == null || item.getType() == Material.AIR) {
                    continue;
                }

                rewardMap.put(i, inv.getItem(i));
            }

            String id = UUID.randomUUID().toString().split("-")[0];

            ItemStack block = new ItemStackBuilder(Material.SAND).setDisplayName(id).build();
            e.getWhoClicked().getInventory().addItem(block);
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage("§6[황무지]§7 입력한 보상을 얻을 수 있는 블럭을 지급하였습니다.");
            feature.addRewardsById(id, rewardMap);
        }
    }
//    @EventHandler
//    public void onInventoryClose(InventoryCloseEvent e) {
//        if(!(e.getInventory() instanceof WasteLandGui)) {
//            return;
//        }
//
//        Inventory inventory = ((WasteLandGui) e.getInventory()).getInventory();
//
//        for(int i=9; i<=17; i++) {
//            if(inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
//                feature.saveGuiItem(i, inventory.getItem(i));
//            }
//        }
//
//        for(int i=36; i<=44; i++) {
//            if(inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
//                feature.saveGuiItem(i, inventory.getItem(i));
//            }
//        }
//    }
}
