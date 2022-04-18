package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.ItemApparel;
import com.github.finley243.adventureengine.world.item.template.ApparelTemplate;

import java.util.*;

public class ApparelComponent {

    public enum ApparelSlot {
        HEAD, TORSO, LEGS
    }

    private final Map<ApparelSlot, ItemApparel> equipped;
    private final Actor actor;

    public ApparelComponent(Actor actor) {
        equipped = new EnumMap<>(ApparelSlot.class);
        this.actor = actor;
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

    public void equip(ItemApparel item) {
        ItemApparel lastEquipped = equipped.get(item.getApparelSlot());
        if(lastEquipped != null) {
            ((ApparelTemplate) lastEquipped.getTemplate()).onUnequip(actor);
        }
        equipped.put(item.getApparelSlot(), item);
        ((ApparelTemplate) item.getTemplate()).onEquip(actor);
    }

    public void unequip(ItemApparel item) {
        if (getEquippedItems().contains(item)) {
            ((ApparelTemplate) item.getTemplate()).onUnequip(actor);
            equipped.put(item.getApparelSlot(), null);
        }
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
