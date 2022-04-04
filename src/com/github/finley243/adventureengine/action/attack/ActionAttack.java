package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandom;
import com.github.finley243.adventureengine.action.reaction.ActionReaction;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.List;

public abstract class ActionAttack extends ActionRandom {

    protected final Actor target;
    protected final ItemWeapon weapon;

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

    protected ActionReaction getReaction(Actor subject) {
        //List<Action> reactions = getWeapon().reactionActions(getTarget());
        //Action chosenAction = target.chooseAction(reactions);
        //return chosenAction instanceof ActionReaction ? (ActionReaction) chosenAction : null;
        return null;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        if(action instanceof ActionAttack) {
            return ((ActionAttack) action).getWeapon() == this.getWeapon();
        } else {
            return false;
        }
    }

}
