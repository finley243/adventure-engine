package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.item.ItemMod;
import com.github.finley243.adventureengine.menu.MenuChoice;

public class ActionModInstall extends Action {

    private final ItemWeapon weapon;
    private final ItemMod mod;

    public ActionModInstall(ItemWeapon weapon, ItemMod mod) {
        this.weapon = weapon;
        this.mod = mod;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getInventory().removeItem(mod);
        weapon.installMod(mod);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && weapon.canInstallMod(mod);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(Inventory.getItemNameFormatted(weapon, subject.getInventory()), canChoose(subject), new String[]{"Inventory", Inventory.getItemNameFormatted(mod, subject.getInventory()), "Install"}, new String[]{"install " + mod.getName() + " on " + weapon.getName()});
    }

}
