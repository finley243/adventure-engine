package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;

public class MenuDataActorInventory extends MenuData {

    public final Actor actor;
    public final Item item;
    public final boolean isStandardTransfer;
    public final boolean isStoreAction;

    public MenuDataActorInventory(Actor actor, Item item, boolean isStandardTransfer, boolean isStoreAction) {
        this.actor = actor;
        this.item = item;
        this.isStandardTransfer = isStandardTransfer;
        this.isStoreAction = isStoreAction;
    }

}
