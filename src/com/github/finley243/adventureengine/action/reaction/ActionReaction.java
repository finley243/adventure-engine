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

    public float getDamageMultOnSuccess() {
        return 0.0f;
    }

    public float getDamageMultOnFail() {
        return 0.0f;
    }

    public float getHitChanceMultOnSuccess() {
        return 0.0f;
    }

    public float getHitChanceMultOnFail() {
        return 0.0f;
    }

    public boolean cancelsAttack() {
        return false;
    }

    public boolean guaranteedHitOnFail() {
        return false;
    }

    public abstract void onSuccess(Actor subject);

    public abstract void onFail(Actor subject);

    public boolean computeSuccess(Actor subject) {
        float chance = chance(subject);
        return chance > ThreadLocalRandom.current().nextFloat();
    }

    public abstract float chance(Actor subject);

    public String getChanceTag(Actor subject) {
        return ((int) Math.ceil(chance(subject)*100)) + "%";
    }

}
