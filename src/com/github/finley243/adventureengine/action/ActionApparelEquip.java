package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.menu.data.MenuDataNested;
import com.github.finley243.adventureengine.world.item.ItemApparel;

public class ActionApparelEquip extends Action {

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
    public MenuData getMenuData(Actor subject) {
        //return new MenuDataInventory("Equip", "Equip " + item.getFormattedName(false), canChoose(subject), item);
        return new MenuDataNested("Equip", "Equip " + item.getFormattedName(false), canChoose(subject), new String[]{"inventory", "apparel", item.getName()});
    }
}
