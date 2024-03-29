package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemMod;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;

public class ActionModRemove extends Action {

    private final ItemWeapon weapon;
    private final ItemMod mod;

    public ActionModRemove(ItemWeapon weapon, ItemMod mod) {
        this.weapon = weapon;
        this.mod = mod;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        weapon.removeMod(mod);
        subject.getInventory().addItem(mod);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventoryCombine(weapon, subject.getInventory(), mod, subject.getInventory());
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Remove";
    }

}
