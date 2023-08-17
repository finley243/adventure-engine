package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;

public class MenuDataAttackArea extends MenuData {

    public final Area target;
    public final ItemWeapon weapon;

    public MenuDataAttackArea(Area target, ItemWeapon weapon) {
        this.target = target;
        this.weapon = weapon;
    }

}
