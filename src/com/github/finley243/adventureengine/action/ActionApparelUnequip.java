package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.world.item.ItemApparel;

public class ActionApparelUnequip implements Action {

    private final ItemApparel item;

    public ActionApparelUnequip(ItemApparel item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.apparelManager().unequip(item, subject);
        subject.inventory().addItem(item);
    }

    @Override
    public String getPrompt() {
        return "Unequip " + item.getFormattedName(false);
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
    public MenuData getMenuData() {
        return new MenuDataEquipped("Unequip", item);
    }
}
