package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class MenuDataAttackTargeted extends MenuData {

    public final AttackTarget target;
    public final Limb limb;
    public final ItemWeapon weapon;

    public MenuDataAttackTargeted(AttackTarget target, Limb limb, ItemWeapon weapon) {
        this.target = target;
        this.limb = limb;
        this.weapon = weapon;
    }

}
