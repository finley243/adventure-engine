package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentArmor;
import com.github.finley243.adventureengine.item.component.ItemComponentEffectible;
import com.github.finley243.adventureengine.item.component.ItemComponentEquippable;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateEquippable;

import java.util.*;

public class EquipmentComponent {

    private final Actor actor;
    private final Map<String, Item> equipped;

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

    public void equip(Item item, ItemComponentTemplateEquippable.EquippableSlotsData slotsData) {
        if (!actor.getEquipSlots().keySet().containsAll(slotsData.slots())) throw new UnsupportedOperationException("Specified equip slots do not exist on actor: " + actor + ", " + slotsData.slots());
        if (!item.getComponentOfType(ItemComponentEquippable.class).isValidEquipData(slotsData)) throw new UnsupportedOperationException("Invalid slots for equipping item: " + item + ", " + slotsData.slots());
        for (String slot : slotsData.slots()) {
            Item lastEquipped = equipped.get(slot);
            if (lastEquipped != null) {
                unequip(lastEquipped);
            }
            equipped.put(slot, item);
        }
        item.getComponentOfType(ItemComponentEquippable.class).onEquip(actor, slotsData);
        if (item.hasComponentOfType(ItemComponentEffectible.class)) {
            for (String equipmentEffect : actor.getEquipmentEffects(item)) {
                item.getComponentOfType(ItemComponentEffectible.class).addEffect(equipmentEffect);
            }
        }
    }

    public void unequip(Item item) {
        if (getEquippedItems().contains(item)) {
            for (String slot : item.getComponentOfType(ItemComponentEquippable.class).getEquippedSlots()) {
                equipped.remove(slot);
            }
            item.getComponentOfType(ItemComponentEquippable.class).onUnequip(actor);
            if (item.hasComponentOfType(ItemComponentEffectible.class)) {
                for (String equipmentEffect : actor.getEquipmentEffects(item)) {
                    item.getComponentOfType(ItemComponentEffectible.class).removeEffect(equipmentEffect);
                }
            }
        }
    }

    public Item getEquippedItemInSlot(String slot) {
        return equipped.get(slot);
    }

    public Set<Item> getEquippedItems() {
        return new HashSet<>(equipped.values());
    }

    public Set<Item> getEquippedWeapons() {
        Set<Item> weapons = new HashSet<>();
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentWeapon.class)) {
                weapons.add(item);
            }
        }
        return weapons;
    }

    public boolean hasRangedWeaponEquipped() {
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentWeapon.class) && item.getComponentOfType(ItemComponentWeapon.class).isRanged()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMeleeWeaponEquipped() {
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentWeapon.class) && !item.getComponentOfType(ItemComponentWeapon.class).isRanged()) {
                return true;
            }
        }
        return false;
    }

    public int getDamageResistanceMain(String damageType) {
        int damageResistance = 0;
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentArmor.class) && item.getComponentOfType(ItemComponentArmor.class).coversMainBody()) {
                damageResistance += item.getComponentOfType(ItemComponentArmor.class).getDamageResistance(damageType);
            }
        }
        return damageResistance;
    }

    public float getDamageMultMain(String damageType) {
        float damageMult = 0;
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentArmor.class) && item.getComponentOfType(ItemComponentArmor.class).coversMainBody()) {
                damageMult += item.getComponentOfType(ItemComponentArmor.class).getDamageMult(damageType);
            }
        }
        return damageMult;
    }

    public int getDamageResistanceLimb(String limb, String damageType) {
        int damageResistance = 0;
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentArmor.class) && item.getComponentOfType(ItemComponentArmor.class).getCoveredLimbs().contains(limb)) {
                damageResistance += item.getComponentOfType(ItemComponentArmor.class).getDamageResistance(damageType);
            }
        }
        return damageResistance;
    }

    public float getDamageMultLimb(String limb, String damageType) {
        float damageMult = 0;
        for (Item item : equipped.values()) {
            if (item.hasComponentOfType(ItemComponentArmor.class) && item.getComponentOfType(ItemComponentArmor.class).getCoveredLimbs().contains(limb)) {
                damageMult += item.getComponentOfType(ItemComponentArmor.class).getDamageMult(damageType);
            }
        }
        return damageMult;
    }

    public List<Action> getEquippedActions() {
        List<Action> actions = new ArrayList<>();
        for (Item item : getEquippedItems()) {
            // TODO - Fix actions for multiple equipped items with the same name
            actions.addAll(item.getComponentOfType(ItemComponentEquippable.class).equippedActions(actor));
        }
        return actions;
    }

}
