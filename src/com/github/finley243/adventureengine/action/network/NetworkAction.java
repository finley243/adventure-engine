package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.Networked;

public abstract class NetworkAction extends Action {

    public abstract float detectionChance();

}
