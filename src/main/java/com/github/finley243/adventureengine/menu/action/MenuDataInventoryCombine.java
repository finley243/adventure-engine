package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.Item;

public class MenuDataInventoryCombine extends MenuData {

    public final Item item;
    public final Inventory inv;
    public final Item combinedItem;
    public final Inventory combinedInv;

    public MenuDataInventoryCombine(Item item, Inventory inv, Item combinedItem, Inventory combinedInv) {
        this.item = item;
        this.inv = inv;
        this.combinedItem = combinedItem;
        this.combinedInv = combinedInv;
    }

}
