package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemMod;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;

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
    public boolean canShow(Actor subject) {
        return super.canShow(subject) && weapon.hasModSlots();
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventoryCombine(weapon, subject.getInventory(), mod, subject.getInventory());
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Install";
    }

}
