package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.Item;

public class MenuDataInventoryCombine extends MenuData {

    public final Item item;
    public final Item combinedItem;

    public MenuDataInventoryCombine(Item item, Item combinedItem) {
        this.item = item;
        this.combinedItem = combinedItem;
    }

}
