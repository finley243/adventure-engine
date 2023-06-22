package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemApparel;

import java.util.*;

public class ApparelComponent {

    private final Actor actor;
    private final Map<String, ItemApparel> equipped;

    public ApparelComponent(Actor actor) {
        this.actor = actor;
        this.equipped = new HashMap<>();
    }

    public boolean isSlotEmpty(String slot) {
        return equipped.containsKey(slot);
    }

    public boolean isSlotEmpty(ItemApparel item) {
        for (String slot : item.getEquipSlots()) {
            if (!isSlotEmpty(slot)) {
                return false;
            }
        }
        return true;
    }

    public void equip(ItemApparel item) {
        for (String slot : item.getEquipSlots()) {
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
            for (String slot : item.getEquipSlots()) {
                equipped.remove(slot);
            }
            item.onUnequip(actor);
        }
    }

    public Set<ItemApparel> getEquippedItems() {
        return new HashSet<>(equipped.values());
    }

    public List<Action> getEquippedActions() {
        List<Action> actions = new ArrayList<>();
        for (ItemApparel item : getEquippedItems()) {
            actions.addAll(item.equippedActions(actor));
        }
        return actions;
    }

}
