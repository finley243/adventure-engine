package com.github.finley243.adventureengine.actor.component;

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
    }

    public void unequip(ItemEquippable item) {
        if (equippedItem != null && equippedItem.equals(item)) {
            equippedItem.setEquippedActor(null);
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
