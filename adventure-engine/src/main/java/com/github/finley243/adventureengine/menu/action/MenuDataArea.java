package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.world.environment.Area;

public class MenuDataArea extends MenuData {

    public final Area area;
    public final boolean isCurrentArea;

    public MenuDataArea(Area area, boolean isCurrentArea) {
        this.area = area;
        this.isCurrentArea = isCurrentArea;
    }

}
