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

    private final Set<String> blockedSlots;

    public EquipmentComponent(Actor actor) {
        this.actor = actor;
        this.equipped = new HashMap<>();
        this.blockedSlots = new HashSet<>();
    }

    public void setSlotBlocked(String slot, boolean blocked) {
        if (!actor.getEquipSlots().containsKey(slot)) throw new UnsupportedOperationException("Specified equip slot does not exist on actor: " + actor + ", " + slot);
        if (blocked) {
            blockedSlots.add(slot);
        } else {
            blockedSlots.remove(slot);
        }
    }

    public boolean isSlotBlocked(String slot) {
        return blockedSlots.contains(slot);
    }

    public boolean isSlotBlocked(Set<String> slots) {
        for (String slot : slots) {
            if (isSlotBlocked(slot)) {
                return true;
            }
        }
        return false;
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
        item.onEquip(actor, slots);
        // TODO - Expand to all equippable items (not just weapons)
        if (item instanceof ItemWeapon weapon) {
            Context context = new Context(actor.game(), actor, actor, item);
            Set<String> equipmentEffects = actor.getStatValue("equipment_effects", context).getValueStringSet(context);
            for (String equipmentEffect : equipmentEffects) {
                weapon.getEffectComponent().addEffect(equipmentEffect);
            }
        }
    }

    public void unequip(ItemEquippable item) {
        if (getEquippedItems().contains(item)) {
            for (String slot : item.getEquippedSlots()) {
                equipped.remove(slot);
            }
            item.onUnequip(actor);
            if (item instanceof ItemWeapon weapon) {
                Context context = new Context(actor.game(), actor, actor, item);
                Set<String> equipmentEffects = actor.getStatValue("equipment_effects", context).getValueStringSet(context);
                for (String equipmentEffect : equipmentEffects) {
                    weapon.getEffectComponent().removeEffect(equipmentEffect);
                }
            }
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
