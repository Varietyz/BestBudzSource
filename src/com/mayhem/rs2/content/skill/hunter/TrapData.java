package com.mayhem.rs2.content.skill.hunter;

import com.mayhem.rs2.entity.item.Item;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Jack
 */
public enum TrapData {
    
    BIRD(9344, 9348, 0, 10006, 0, 5548, 0, 100, new Item(526, 1), new Item(9978, 1), new Item(10087, 3)),
    SALAMANDER(9002, 9004, 0, 305, 0, 2906, 60, 200, new Item(10149, 1)),
    CHINCHOMPA(9380 , 9384, 0, 10008, 2911, 2911, 80, 300, new Item(9977, 2));
    
    private final int setupObject;
    private final int activeObject;
    private final int returnObject;
    private final Item itemId;
    private final Item baitId;
    private final int attractMob;
    private final int level;
    private final int experience;
    private final List<Item> rewards;

    private TrapData(int setupObject, int activeObject, int returnObject, int itemId, int baitId, int attractMob, int level, int experience, Item... rewards) {
        this.setupObject = setupObject;
        this.activeObject = activeObject;
        this.returnObject = returnObject;
        this.itemId = new Item(itemId, 1);
        this.baitId = new Item(baitId, 1);
        this.attractMob = attractMob;
        this.level = level;
        this.experience = experience;
        this.rewards = Arrays.stream(rewards).collect(Collectors.toList());
    }

    public int getSetupObject() {
        return setupObject;
    }

    public int getActiveObject() {
        return activeObject;
    }

    public Item getItemId() {
        return itemId;
    }

    public List<Item> getRewards() {
        return rewards;
    }

    public Item getBaitId() {
        return baitId;
    }

    public int getAttractMob() {
        return attractMob;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getReturnObject() {
        return returnObject == 0 ? 95 : returnObject;
    }
    
    public static Optional<TrapData> getDataForItemId(int itemId) {
        return Arrays.stream(values())
                .filter(data -> data.getItemId().getId() == itemId)
                .findAny();
    }
    
    public static Optional<TrapData> getDataForActiveObject(int object) {
        return Arrays.stream(values())
                .filter(data -> data.getActiveObject() == object)
                .findAny();
    }
    
    public static boolean isHunterMob(int mobId) {
        return Arrays.stream(values()).anyMatch(data -> data.getAttractMob() == mobId);
    }
}
