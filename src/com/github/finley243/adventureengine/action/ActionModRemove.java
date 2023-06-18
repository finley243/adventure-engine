package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.item.ItemWeaponMod;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;

public class ActionModRemove extends Action {

    private final ItemWeapon weapon;
    private final ItemWeaponMod mod;

    public ActionModRemove(ItemWeapon weapon, ItemWeaponMod mod) {
        this.weapon = weapon;
        this.mod = mod;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        weapon.removeMod(mod);
        subject.getInventory().addItem(mod);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Remove", canChoose(subject), new String[]{"Inventory", Inventory.getItemNameFormatted(weapon, subject.getInventory()), "Mods", LangUtils.titleCase(mod.getName())}, new String[]{"remove " + mod.getName() + " from " + weapon.getName()});
    }

}
