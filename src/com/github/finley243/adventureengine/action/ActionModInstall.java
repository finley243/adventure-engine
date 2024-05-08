package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentModdable;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;

public class ActionModInstall extends Action {

    private final Item target;
    private final Item mod;

    public ActionModInstall(Item target, Item mod) {
        this.target = target;
        this.mod = mod;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getInventory().removeItem(mod);
        target.getComponentOfType(ItemComponentModdable.class).installMod(mod);
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (!target.getComponentOfType(ItemComponentModdable.class).canInstallMod(mod)) {
            return new CanChooseResult(false, "Cannot be installed on this item");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public boolean canShow(Actor subject) {
        return super.canShow(subject) && target.hasComponentOfType(ItemComponentModdable.class);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventoryCombine(target, subject.getInventory(), mod, subject.getInventory());
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Install";
    }

}
