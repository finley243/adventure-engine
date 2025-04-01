package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.Item;

public class MenuDataInventory extends MenuData {

    public final Item item;
    public final Inventory inv;

    public MenuDataInventory(Item item, Inventory inv) {
        this.item = item;
        this.inv = inv;
    }

}
