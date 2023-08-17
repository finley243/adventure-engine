package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class MenuDataObjectInventory extends MenuData {

    public final WorldObject object;
    public final Item item;
    public final boolean isStoreAction;

    public MenuDataObjectInventory(WorldObject object, Item item, boolean isStoreAction) {
        this.object = object;
        this.item = item;
        this.isStoreAction = isStoreAction;
    }

}
