package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class MenuDataMove extends MenuData {

    public final Area destination;
    public final AreaLink.CompassDirection direction;

    public MenuDataMove(Area destination, AreaLink.CompassDirection direction) {
        this.destination = destination;
        this.direction = direction;
    }

}
