package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.world.item.ItemApparel;

import java.util.*;

public class ApparelManager {

    public enum ApparelSlot {
        HEAD, TORSO, LEGS
    }

    private final Map<ApparelSlot, ItemApparel> equipped;

    public ApparelManager() {
        equipped = new EnumMap<>(ApparelSlot.class);
    }

    public int getDamageResistance(ApparelSlot slot) {
        if(isSlotEmpty(slot)) return 0;
        return equipped.get(slot).getDamageResistance();
    }

    public boolean isSlotEmpty(ApparelSlot slot) {
        return equipped.get(slot) == null;
    }

    public boolean isSlotEmpty(ItemApparel item) {
        return isSlotEmpty(item.getApparelSlot());
    }

    public void equip(ItemApparel item, Actor subject) {
        ItemApparel lastEquipped = equipped.get(item.getApparelSlot());
        if(lastEquipped != null) {
            lastEquipped.unequip(subject);
        }
        equipped.put(item.getApparelSlot(), item);
        item.equip(subject);
    }

    public void unequip(ItemApparel item, Actor subject) {
        item.unequip(subject);
        equipped.put(item.getApparelSlot(), null);
    }

    public Set<ItemApparel> getEquippedItems() {
        Set<ItemApparel> items = new HashSet<>();
        for(ItemApparel item : equipped.values()) {
            if(item != null) {
                items.add(item);
            }
        }
        return items;
    }

}
