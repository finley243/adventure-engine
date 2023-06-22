package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemWeapon;

import java.util.*;

public class EquipmentComponent {

    private final Actor actor;
    private final Map<String, ItemEquippable> equipped;

    public EquipmentComponent(Actor actor) {
        this.actor = actor;
        this.equipped = new HashMap<>();
    }

    public boolean isSlotEmpty(String slot) {
        return !equipped.containsKey(slot);
    }

    public boolean isSlotEmpty(ItemEquippable item) {
        for (String slot : item.getEquipSlots()) {
            if (!isSlotEmpty(slot)) {
                return false;
            }
        }
        return true;
    }

    public void equip(ItemEquippable item) {
        for (String slot : item.getEquipSlots()) {
            ItemEquippable lastEquipped = equipped.get(slot);
            if (lastEquipped != null) {
                unequip(lastEquipped);
            }
            equipped.put(slot, item);
        }
        // TODO - Expand to all equippable items
        if (item instanceof ItemWeapon weapon) {
            for (String equipmentEffect : actor.getValueStringSet("equipment_effects", new Context(actor.game(), actor, actor, item))) {
                weapon.getEffectComponent().addEffect(equipmentEffect);
            }
        }
        item.onEquip(actor);
    }

    public void unequip(ItemEquippable item) {
        if (getEquippedItems().contains(item)) {
            for (String slot : item.getEquipSlots()) {
                equipped.remove(slot);
            }
            if (item instanceof ItemWeapon weapon) {
                for (String equipmentEffect : actor.getValueStringSet("equipment_effects", new Context(actor.game(), actor, actor, item))) {
                    weapon.getEffectComponent().removeEffect(equipmentEffect);
                }
            }
            item.onUnequip(actor);
        }
    }

    public ItemEquippable getEquippedItemInSlot(String slot) {
        return equipped.get(slot);
    }

    public Set<ItemEquippable> getEquippedItems() {
        return new HashSet<>(equipped.values());
    }

    public Set<ItemWeapon> getEquippedWeapons() {
        Set<ItemWeapon> weapons = new HashSet<>();
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemWeapon weapon) {
                weapons.add(weapon);
            }
        }
        return weapons;
    }

    public boolean hasRangedWeaponEquipped() {
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemWeapon weapon && weapon.isRanged()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMeleeWeaponEquipped() {
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemWeapon weapon && !weapon.isRanged()) {
                return true;
            }
        }
        return false;
    }

    public List<Action> getEquippedActions() {
        List<Action> actions = new ArrayList<>();
        for (ItemEquippable item : getEquippedItems()) {
            actions.addAll(item.equippedActions(actor));
        }
        return actions;
    }

}
