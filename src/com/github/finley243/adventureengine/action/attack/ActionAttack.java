package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandom;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public abstract class ActionAttack extends ActionRandom {

    private final Actor target;
    private final ItemWeapon weapon;

    public ActionAttack(ItemWeapon weapon, Actor target) {
        this.weapon = weapon;
        this.target = target;
    }

    public ItemWeapon getWeapon() {
        return weapon;
    }

    public Actor getTarget() {
        return target;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        if(action instanceof ActionAttack) {
            return ((ActionAttack) action).getWeapon() == this.getWeapon();
        } else {
            return false;
        }
    }

}
