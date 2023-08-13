package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
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
        subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (!weapon.canInstallMod(mod)) {
            return new CanChooseResult(false, "Cannot be installed on this item");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(Inventory.getItemNameFormatted(weapon, subject.getInventory()), canChoose(subject).canChoose(), new String[]{"Inventory", Inventory.getItemNameFormatted(mod, subject.getInventory()), "Install"}, new String[]{"install " + mod.getName() + " on " + weapon.getName()});
    }

}
