package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.world.object.WorldObject;

public class MenuDataObject extends MenuData {

    public final WorldObject object;

    public MenuDataObject(WorldObject object) {
        this.object = object;
    }

}
