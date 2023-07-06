package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemArmor;
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

    public boolean isSlotEmpty(Set<String> slots) {
        for (String slot : slots) {
            if (!isSlotEmpty(slot)) {
                return false;
            }
        }
        return true;
    }

    public void equip(ItemEquippable item, Set<String> slots) {
        if (!actor.getEquipSlots().keySet().containsAll(slots)) throw new UnsupportedOperationException("Specified equip slots do not exist on actor: " + actor + ", " + slots);
        if (!item.getEquipSlots().contains(slots)) throw new UnsupportedOperationException("Invalid slots for equipping item: " + item + ", " + slots);
        for (String slot : slots) {
            ItemEquippable lastEquipped = equipped.get(slot);
            if (lastEquipped != null) {
                unequip(lastEquipped);
            }
            equipped.put(slot, item);
        }
        // TODO - Expand to all equippable items
        Context context = new Context(actor.game(), actor, actor, item);
        if (item instanceof ItemWeapon weapon) {
            for (String equipmentEffect : actor.getStatValue("equipment_effects", context).getValueStringSet(context)) {
                weapon.getEffectComponent().addEffect(equipmentEffect);
            }
        }
        item.onEquip(actor, slots);
    }

    public void unequip(ItemEquippable item) {
        if (getEquippedItems().contains(item)) {
            for (String slot : item.getEquippedSlots()) {
                equipped.remove(slot);
            }
            Context context = new Context(actor.game(), actor, actor, item);
            if (item instanceof ItemWeapon weapon) {
                for (String equipmentEffect : actor.getStatValue("equipment_effects", context).getValueStringSet(context)) {
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

    public int getDamageResistanceMain(String damageType) {
        int damageResistance = 0;
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemArmor armorItem && armorItem.coversMainBody()) {
                damageResistance += armorItem.getDamageResistance(damageType);
            }
        }
        return damageResistance;
    }

    public float getDamageMultMain(String damageType) {
        float damageMult = 0;
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemArmor armorItem && armorItem.coversMainBody()) {
                damageMult += armorItem.getDamageMult(damageType);
            }
        }
        return damageMult;
    }

    public int getDamageResistanceLimb(String limb, String damageType) {
        int damageResistance = 0;
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemArmor armorItem && armorItem.getCoveredLimbs().contains(limb)) {
                damageResistance += armorItem.getDamageResistance(damageType);
            }
        }
        return damageResistance;
    }

    public float getDamageMultLimb(String limb, String damageType) {
        float damageMult = 0;
        for (ItemEquippable item : equipped.values()) {
            if (item instanceof ItemArmor armorItem && armorItem.getCoveredLimbs().contains(limb)) {
                damageMult += armorItem.getDamageMult(damageType);
            }
        }
        return damageMult;
    }

    public List<Action> getEquippedActions() {
        List<Action> actions = new ArrayList<>();
        for (ItemEquippable item : getEquippedItems()) {
            // TODO - Fix actions for multiple equipped items with the same name
            actions.addAll(item.equippedActions(actor));
        }
        return actions;
    }

}
