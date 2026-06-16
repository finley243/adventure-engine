package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

public class MenuDataInventoryCombine extends MenuData {

    public final Item item;
    public final Inventory inv;
    public final Item combinedItem;
    public final ItemTemplate combinedItemTemplate;
    public final Inventory combinedInv;

    public MenuDataInventoryCombine(Item item, Inventory inv, Item combinedItem, ItemTemplate combinedItemTemplate, Inventory combinedInv) {
        this.item = item;
        this.inv = inv;
        this.combinedItem = combinedItem;
        this.combinedItemTemplate = combinedItemTemplate;
        this.combinedInv = combinedInv;
    }

}
