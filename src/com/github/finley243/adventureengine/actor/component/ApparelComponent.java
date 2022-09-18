package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.item.ItemApparel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApparelComponent {

    private final Actor actor;
    private final Map<String, ItemApparel> equipped;

    public ApparelComponent(Actor actor) {
        this.actor = actor;
        this.equipped = new HashMap<>();
    }

    public int getDamageResistance(String slot, Damage.DamageType type) {
        if(isSlotEmpty(slot)) return 0;
        return equipped.get(slot).getDamageResistance(type);
    }

    public boolean isSlotEmpty(String slot) {
        return equipped.get(slot) == null;
    }

    public boolean isSlotEmpty(ItemApparel item) {
        for (String slot : item.getApparelSlots()) {
            if (!isSlotEmpty(slot)) {
                return false;
            }
        }
        return true;
    }

    public void equip(ItemApparel item) {
        for (String slot : item.getApparelSlots()) {
            ItemApparel lastEquipped = equipped.get(slot);
            if (lastEquipped != null) {
                unequip(lastEquipped);
            }
            equipped.put(slot, item);
        }
        item.onEquip(actor);
    }

    public void unequip(ItemApparel item) {
        if (getEquippedItems().contains(item)) {
            for (String slot : item.getApparelSlots()) {
                equipped.put(slot, null);
            }
            item.onUnequip(actor);
        }
    }

    public Set<ItemApparel> getEquippedItems() {
        Set<ItemApparel> items = new HashSet<>(equipped.values());
        items.remove(null);
        return items;
    }

}
