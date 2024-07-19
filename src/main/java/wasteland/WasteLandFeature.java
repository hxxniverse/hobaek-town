package wasteland;

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WasteLandFeature implements BaseFeature {
    public static WasteLandFeature INSTANCE;
    private WasteLandBrushGUI BRUSH_GUI;

    private final Map<Location, String> locIdMap = new HashMap<>();
    private final Map<String, Map<Integer, ItemStack>> idRewardMap = new HashMap<>();

//    private final Map<Integer, ItemStack> guiItemMap = new HashMap<>();
    private final Map<ItemStack, Integer> brushLevelMap = new HashMap<>();
    private final Map<Location, Material> brushedBlocks = new HashMap<>();

    @Override
    public void onEnable(@NotNull JavaPlugin plugin) {
        INSTANCE = this;

        WasteLandCommand command = new WasteLandCommand();
        command.register(plugin);

        loadData();

        int count = optimizeData();
        if(count > 0) System.out.println("§6[황무지]§r 자동으로 " + count + "개의 더미 데이터를 최적화 하였습니다.");

        BRUSH_GUI = new WasteLandBrushGUI();
    }

    @Override
    public void onDisable(@NotNull JavaPlugin plugin) {
        saveData();

        // 블럭이 바뀌는 3분 동안 서버(플러그인)가 종료되는 경우 즉시 변경 실행
        Block block;
        for (Map.Entry<Location, Material> entry : brushedBlocks.entrySet()) {
            block = entry.getKey().getBlock();

            // 해당 블럭이 없어졌거나, 이미 정상값과 일치하는 경우 Continue
            if (block.getType() == Material.AIR || block.getType() == entry.getValue()) {
                continue;
            }

            block.setType(entry.getValue());
        }
    }

    public Map<ItemStack, Integer> getBrushLevelMap() {
        return brushLevelMap;
    }

    public Map<Location, Material> getBrushedBlocks() {
        return brushedBlocks;
    }

    public void addRewardsById(String id, Map<Integer, ItemStack> rewardMap) {
        this.idRewardMap.put(id, rewardMap);
    }

    public Map<Integer, ItemStack> getRewardsById(String id) {
        return this.idRewardMap.get(id);
    }

    public void addIdToLocation(Location loc, String id) {
        locIdMap.put(loc, id);
    }

    public void deleteIdFromLocation(Location loc) {
        locIdMap.remove(loc);
    }

    @Nullable
    public String getIdByLocation(Location loc) {
        return locIdMap.getOrDefault(loc, null);
    }

    // 버그 클리어 및 더미 데이터 최적화
    public int optimizeData() {
        int count = 0;

        // locIdMap 버그 제거 작업 수행
        Iterator<Map.Entry<Location, String>> iterator1 = locIdMap.entrySet().iterator();
        Map.Entry<Location, String> entry1;
        Block block;

        while(iterator1.hasNext()) {
            entry1 = iterator1.next();
            block = entry1.getKey().getBlock();

            if (block.getType() != Material.SAND && block.getType() != Material.GRAVEL) {
                iterator1.remove();
                count++;
            }
        }

        // locIdMap 의 모든 값을 Set 으로 수집
        Set<String> usedKeys = new HashSet<>(locIdMap.values());

        // idRewardMap 의 키를 루프하며 usedKeys 에 없는 키를 제거
        Iterator<Map.Entry<String, Map<Integer, ItemStack>>> iterator2 = idRewardMap.entrySet().iterator();
        Map.Entry<String, Map<Integer, ItemStack>> entry2;

        while(iterator2.hasNext()) {
            entry2 = iterator2.next();
            if (!usedKeys.contains(entry2.getKey())) {
                iterator2.remove();
                count++;
            }
        }

        return count;
    }

    public void saveData() {
        // guiMap -> 영구저장소
        WasteLandConfig.INSTANCE.updateConfigData(originalData -> originalData.copy(
                locIdMap,
                idRewardMap,
                brushLevelMap
            )
        );
    }

    public void loadData() {
        // 영구저장소 -> guiMap
        WasteLandConfig.INSTANCE.load();
        locIdMap.putAll(WasteLandConfig.INSTANCE.getConfigData().getLocIdMap());
        idRewardMap.putAll(WasteLandConfig.INSTANCE.getConfigData().getIdRewardMap());
        brushLevelMap.putAll(WasteLandConfig.INSTANCE.getConfigData().getBrushLevelMap());
    }

    public void setBrushLevel(ItemStack item, int level) {
        // item 에 level 을 부여하여 영구저장소에 저장
        if(item.getType() != Material.BRUSH) {
            return;
        }

        brushLevelMap.put(item, level);
    }

    public int getBrushLevel(ItemStack item) {
        return brushLevelMap.getOrDefault(item, -1);
    }

    public void deleteBrush(ItemStack item) {
        brushLevelMap.remove(item);
    }

    public ItemStack getRandomItem(Map<Integer, ItemStack> indexItemMap, int level) {
        // <ItemStack, 가중치> 형태의 가중치 Map
        Map<ItemStack, Double> map = new HashMap<>();

        // guiItemMap 에서 ItemStack 을 뽑아 map 으로 put 하며 가중치 부여
        for (Map.Entry<Integer, ItemStack> entry : indexItemMap.entrySet()) {
            // 솔 등급에 따라 확률적으로 받을 수 있는 아이템 거르기
            if (entry.getKey() % 9 >= level) {
                continue;
            }

//            // 자갈을 솔질한 경우 자갈로 얻을 수 있는 아이템만 map 에 등록
//            if(material == Material.GRAVEL && entry.getKey() <= 17) {
//                map.put(entry.getValue(), getWeight(entry.getKey()));
//            }
//
//            // 모래를 솔질한 경우 자갈로 얻을 수 있는 아이템만 map 에 등록
//            if(material == Material.SAND && entry.getKey() >= 36) {
//                map.put(entry.getValue(), getWeight(entry.getKey()));
//            }

            map.put(entry.getValue(), getWeight(entry.getKey()));
        }

        // map 에 등록된 아이템이 없을 경우 리턴 (이론상 불가능 / 버그 방지)
        if(map.isEmpty()) return new ItemStack(Material.AIR);

        Random random = new Random();
        double totalWeight = 0.0D;

        // 총 가중치 합 계산
        for (Double weight : map.values()) {
            totalWeight += weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0D;

        // 가중치에 기반하여 랜덤한 ItemStack 뽑기
        for (Map.Entry<ItemStack, Double> entry : map.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }

        // 랜덤으로 ItemStack 을 획득하지 못했을 경우 (이론상 불가능)
        return new ItemStack(Material.AIR);
    }

    public Inventory createGUI() {
        return new WasteLandGUI().getInventory();
    }

    public Inventory openBrushGUI() {
        return BRUSH_GUI.getInventory();
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