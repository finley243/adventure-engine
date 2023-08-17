package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class MenuDataAttack extends MenuData {

    public final AttackTarget target;
    public final ItemWeapon weapon;

    public MenuDataAttack(AttackTarget target, ItemWeapon weapon) {
        this.target = target;
        this.weapon = weapon;
    }

}
