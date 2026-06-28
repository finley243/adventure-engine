package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.AttackTarget;

public class MenuDataAttack extends MenuData {

    public final AttackTarget target;
    public final Item weapon;

    public MenuDataAttack(AttackTarget target, Item weapon) {
        this.target = target;
        this.weapon = weapon;
    }

}
