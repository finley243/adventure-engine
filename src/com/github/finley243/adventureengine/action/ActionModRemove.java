package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.item.ItemMod;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
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
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
    }

    @Override
    public ActionCategory getCategory(Actor subject) {
        return ActionCategory.INVENTORY_COMBINE;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventoryCombine(weapon, mod);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Remove";
    }

}
