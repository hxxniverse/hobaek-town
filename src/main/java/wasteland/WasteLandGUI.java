package wasteland;

import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WasteLandGUI implements InventoryHolder {
    private final Inventory inventory;

    public WasteLandGUI() {
        inventory = Bukkit.createInventory(this, 27, "황무지 아이템설정");

        // GUI 구성1 : 유리판 경계 생성
        ItemStack glass = new ItemStackBuilder(Material.GLASS_PANE).build();
        for(int i=9 ; i<=17 ; i++) inventory.setItem(i, glass);
        for(int i=45 ; i<=53 ; i++) inventory.setItem(i, glass);

        // GUI 구성2 : 색 유리판 경계 생성
        inventory.setItem(0, new ItemStackBuilder(Material.RED_STAINED_GLASS).setDisplayName("§r30%").build());
        inventory.setItem(1, new ItemStackBuilder(Material.ORANGE_STAINED_GLASS).setDisplayName("§r25%").build());
        inventory.setItem(2, new ItemStackBuilder(Material.YELLOW_STAINED_GLASS).setDisplayName("§r15%").build());
        inventory.setItem(3, new ItemStackBuilder(Material.GREEN_STAINED_GLASS).setDisplayName("§r10%").build());
        inventory.setItem(4, new ItemStackBuilder(Material.BLUE_STAINED_GLASS).setDisplayName("§r8%").build());
        inventory.setItem(5, new ItemStackBuilder(Material.PURPLE_STAINED_GLASS).setDisplayName("§r6%").build());
        inventory.setItem(6, new ItemStackBuilder(Material.BROWN_STAINED_GLASS).setDisplayName("§r4%").build());
        inventory.setItem(7, new ItemStackBuilder(Material.BLACK_STAINED_GLASS).setDisplayName("§r1.5%").build());
        inventory.setItem(8, new ItemStackBuilder(Material.WHITE_STAINED_GLASS).setDisplayName("§r0.5%").build());

        inventory.setItem(27, new ItemStackBuilder(Material.RED_STAINED_GLASS).setDisplayName("§r30%").build());
        inventory.setItem(28, new ItemStackBuilder(Material.ORANGE_STAINED_GLASS).setDisplayName("§r25%").build());
        inventory.setItem(29, new ItemStackBuilder(Material.YELLOW_STAINED_GLASS).setDisplayName("§r15%").build());
        inventory.setItem(30, new ItemStackBuilder(Material.GREEN_STAINED_GLASS).setDisplayName("§r10%").build());
        inventory.setItem(31, new ItemStackBuilder(Material.BLUE_STAINED_GLASS).setDisplayName("§r8%").build());
        inventory.setItem(32, new ItemStackBuilder(Material.PURPLE_STAINED_GLASS).setDisplayName("§r6%").build());
        inventory.setItem(33, new ItemStackBuilder(Material.BROWN_STAINED_GLASS).setDisplayName("§r4%").build());
        inventory.setItem(34, new ItemStackBuilder(Material.BLACK_STAINED_GLASS).setDisplayName("§r1.5%").build());
        inventory.setItem(35, new ItemStackBuilder(Material.WHITE_STAINED_GLASS).setDisplayName("§r0.5%").build());

        // guiMap 에서 inventory 로 ItemStack 불러오기
//        Map<Integer, ItemStack> map = WasteLandFeature.INSTANCE.getGuiMap();
//        for(Map.Entry<Integer, ItemStack> entry : map.entrySet()) {
//            inventory.setItem(entry.getKey(), entry.getValue());
//        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}