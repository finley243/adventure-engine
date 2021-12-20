package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.item.ItemApparel;

public class ActionApparelEquip extends Action {

    private final ItemApparel item;

    public ActionApparelEquip(ItemApparel item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.inventory().removeItem(item);
        subject.equipmentComponent().equip(item);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        //return new MenuDataInventory("Equip", "Equip " + item.getFormattedName(false), canChoose(subject), item);
        return new MenuData("Equip", "Equip " + item.getFormattedName(false), canChoose(subject), new String[]{"inventory", item.getName()});
    }
}
