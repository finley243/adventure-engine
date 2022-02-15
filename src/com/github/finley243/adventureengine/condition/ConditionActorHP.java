package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorHP implements Condition {

    private final ActorReference actor;
    private final Equality equality;
    private final float value;

    public ConditionActorHP(ActorReference actor, Equality equality, float value) {
        this.actor = actor;
        this.equality = equality;
        this.value = value;
    }

    @Override
    public boolean isMet(Actor subject) {
        float healthProportion = actor.getActor(subject).getHPProportion();
        switch(equality) {
            case LESS:
                return healthProportion < value;
            case GREATER:
                return healthProportion > value;
            case LESS_EQUAL:
                return healthProportion <= value;
            case GREATER_EQUAL:
                return healthProportion >= value;
            case EQUAL:
                return healthProportion == value;
            case NOT_EQUAL:
                return healthProportion != value;
            default:
                return false;
        }
    }

    @Override
    public String getChoiceTag() {
        return null;
    }

}
