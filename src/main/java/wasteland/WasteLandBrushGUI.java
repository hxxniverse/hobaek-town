package wasteland;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WasteLandBrushGUI implements InventoryHolder {
    private final Inventory inventory;

    public WasteLandBrushGUI() {
        inventory = Bukkit.createInventory(this, 54, "솔 목록 (좌클 받기, 쉬프트+우클 삭제)");

        Map<ItemStack, Integer> map = WasteLandFeature.INSTANCE.getBrushLevelMap();
        inventory.addItem(map.keySet().toArray(new ItemStack[0]));
    }

    public void reloadGUI() {
        inventory.clear();

        Map<ItemStack, Integer> map = WasteLandFeature.INSTANCE.getBrushLevelMap();
        inventory.addItem(map.keySet().toArray(new ItemStack[0]));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
