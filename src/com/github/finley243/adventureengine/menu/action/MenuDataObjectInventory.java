package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class MenuDataObjectInventory extends MenuData {

    public final WorldObject object;
    public final Item item;
    public final String inventoryName;
    public final boolean isStandardTransfer;
    public final boolean isStoreAction;

    public MenuDataObjectInventory(WorldObject object, Item item, String inventoryName, boolean isStandardTransfer, boolean isStoreAction) {
        this.object = object;
        this.item = item;
        this.inventoryName = inventoryName;
        this.isStandardTransfer = isStandardTransfer;
        this.isStoreAction = isStoreAction;
    }

}
