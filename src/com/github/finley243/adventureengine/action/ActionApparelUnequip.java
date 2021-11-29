package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.world.item.ItemApparel;

public class ActionApparelUnequip extends Action {

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
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory("Unequip", "Unequip " + item.getFormattedName(false), canChoose(subject), item);
    }
}
