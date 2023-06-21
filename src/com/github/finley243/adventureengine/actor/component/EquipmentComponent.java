package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class EquipmentComponent {

    private final Actor actor;
    private ItemEquippable equippedItem;

    public EquipmentComponent(Actor actor) {
        this.actor = actor;
    }

    public void equip(ItemEquippable item) {
        equippedItem = item;
        equippedItem.setEquippedActor(actor);
        // TODO - Expand to all equippable items
        if (equippedItem instanceof ItemWeapon weapon) {
            for (String equipmentEffect : actor.getValueStringSet("equipment_effects", new Context(actor.game(), actor, actor, equippedItem))) {
                weapon.getEffectComponent().addEffect(equipmentEffect);
            }
        }
    }

    public void unequip(ItemEquippable item) {
        if (equippedItem != null && equippedItem.equals(item)) {
            equippedItem.setEquippedActor(null);
            if (equippedItem instanceof ItemWeapon weapon) {
                for (String equipmentEffect : actor.getValueStringSet("equipment_effects", new Context(actor.game(), actor, actor, equippedItem))) {
                    weapon.getEffectComponent().removeEffect(equipmentEffect);
                }
            }
            this.equippedItem = null;
        }
    }

    public ItemEquippable getEquippedItem() {
        return equippedItem;
    }

    public boolean hasEquippedItem() {
        return equippedItem != null;
    }

    public boolean hasRangedWeaponEquipped() {
        return equippedItem != null && equippedItem instanceof ItemWeapon && ((ItemWeapon) equippedItem).isRanged();
    }

    public boolean hasMeleeWeaponEquipped() {
        return equippedItem != null && equippedItem instanceof ItemWeapon && !((ItemWeapon) equippedItem).isRanged();
    }

}
