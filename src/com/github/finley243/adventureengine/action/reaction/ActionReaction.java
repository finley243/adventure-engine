package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionReaction extends Action {

    protected final Actor attacker;
    protected final ItemWeapon weapon;

    public ActionReaction(Actor attacker, ItemWeapon weapon) {
        this.attacker = attacker;
        this.weapon = weapon;
    }

    @Override
    public void choose(Actor subject) {}

    public float runDamageReduction(Actor subject) {
        if(ThreadLocalRandom.current().nextFloat() < chance(subject)) {
            return onSuccess(subject);
        } else {
            return onFail(subject);
        }
    }

    public abstract float onSuccess(Actor subject);

    public abstract float onFail(Actor subject);

    public abstract float chance(Actor subject);

}
