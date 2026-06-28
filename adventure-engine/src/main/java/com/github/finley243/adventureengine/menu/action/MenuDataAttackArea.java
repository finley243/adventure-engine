package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.environment.Area;

public class MenuDataAttackArea extends MenuData {

    public final Area target;
    public final Item weapon;

    public MenuDataAttackArea(Area target, Item weapon) {
        this.target = target;
        this.weapon = weapon;
    }

}
