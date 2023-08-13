package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.item.ItemMod;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;

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
        subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Remove", canChoose(subject).canChoose(), new String[]{"Inventory", Inventory.getItemNameFormatted(weapon, subject.getInventory()), "Mods", LangUtils.titleCase(mod.getName())}, new String[]{"remove " + mod.getName() + " from " + weapon.getName()});
    }

}
