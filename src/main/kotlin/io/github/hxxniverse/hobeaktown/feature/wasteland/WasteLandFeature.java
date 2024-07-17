package io.github.hxxniverse.hobeaktown.feature.wasteland;

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WasteLandFeature implements BaseFeature {
    private static WasteLandFeature instance;
    private final Map<Integer, ItemStack> guiItemMap = new HashMap<>();
    private final Map<Location, Material> brushedBlocks = new HashMap<>();

    private WasteLandGui gui;

    @Override
    public void onEnable(@NotNull JavaPlugin plugin) {
        instance = this;
        gui = new WasteLandGui();
        loadData();
    }

    @Override
    public void onDisable(@NotNull JavaPlugin plugin) {
        saveData();

        // 블럭이 바뀌는 3분 동안 서버(플러그인)가 종료되는 경우 즉시 변경 실행
        Block block;
        for(Map.Entry entry : brushedBlocks.entrySet()) {
            block = ((Location) entry.getKey()).getBlock();

            if(block.getType() == Material.AIR) {
                continue;
            }

            block.setType((Material) entry.getValue());
        }
    }

    public static WasteLandFeature getInstance() {
        return instance;
    }

    public void saveGuiItem(int index, ItemStack item) {
        guiItemMap.put(index, item);
    }

    public Map<Integer, ItemStack> getGuiMap() {
        return guiItemMap;
    }

    public Map<Location, Material> getBrushedBlocks() {
        return brushedBlocks;
    }

    public void saveData() {
        // TODO (데이터 I/O 관련 API 학습 필요)
        // guiMap -> 영구저장소

    }

    public void loadData() {
        // TODO (데이터 I/O 관련 API 학습 필요)
        // 영구저장소 -> guiMap
    }

    public void setBrushLevel(ItemStack item, int level) {
        // TODO (데이터 I/O 관련 API 학습 필요)
        // item 에 level 을 부여하여 영구저장소에 저장
    }

    public boolean isConstructionSite(Location location) {
        // TODO (Area 관련 API 필요)
        return true;
    }

    public ItemStack getRandomItem(int level) {
        // <ItemStack, 가중치> 형태의 가중치 Map
        Map<ItemStack, Double> map = new HashMap<>();

        // guiItemMap 에서 ItemStack 을 뽑아 map 으로 put 하며 가중치 부여
        for(Map.Entry<Integer, ItemStack> entry : guiItemMap.entrySet()) {
            // 솔 등급에 따라 확률적으로 받을 수 있는 아이템 거르기
            if(entry.getKey() % 9 >= level) {
                continue;
            }

            map.put(entry.getValue(), getWeight(entry.getKey()));
        }

        Random random = new Random();
        double totalWeight = 0.0D;

        // 총 가중치 합 계산
        for(Double weight : map.values()) {
            totalWeight += weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0D;

        // 가중치에 기반하여 랜덤한 ItemStack 뽑기
        for(Map.Entry<ItemStack, Double> entry : map.entrySet()) {
            cumulativeWeight += entry.getValue();
            if(randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }

        // 랜덤으로 ItemStack 을 획득하지 못했을 경우 (이론상 불가능)
        return new ItemStack(Material.AIR);
    }

    public Inventory getGui() {
        return gui.getInventory();
    }

    private double getWeight(int i) {
        return switch (i) {
            case 9, 36 -> 0.30D;
            case 10, 37 -> 0.25D;
            case 11, 38 -> 0.15D;
            case 12, 39 -> 0.10D;
            case 13, 40 -> 0.08D;
            case 14, 41 -> 0.06D;
            case 15, 42 -> 0.04D;
            case 16, 43 -> 0.015D;
            case 17, 44 -> 0.005D;
            default -> 0D;
        };

    }
}