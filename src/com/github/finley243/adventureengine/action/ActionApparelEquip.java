package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.world.item.ItemApparel;

public class ActionApparelEquip implements Action {

    private boolean disabled;
    private final ItemApparel item;

    public ActionApparelEquip(ItemApparel item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.inventory().removeItem(item);
        subject.apparelManager().equip(item, subject);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return !disabled;
    }

    @Override
    public void disable() {
        disabled = true;
    }

    @Override
    public String getPrompt() {
        return "Equip " + item.getFormattedName(false);
    }

    @Override
    public float utility(Actor subject) {
        return 0;
    }

    @Override
    public boolean usesAction() {
        return true;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return false;
    }

    @Override
    public int actionCount() {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory("Equip", canChoose(subject), item);
    }
}
