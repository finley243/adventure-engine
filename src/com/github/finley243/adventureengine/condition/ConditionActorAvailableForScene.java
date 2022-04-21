package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorAvailableForScene extends Condition {

	private final ActorReference actor;
	
	public ConditionActorAvailableForScene(boolean invert, ActorReference actor) {
		super(invert);
		if(actor.isPlayer()) throw new IllegalArgumentException("ConditionActorAvailableForScene must have a non-player actor reference");
		this.actor = actor;
	}

	@Override
	public boolean isMet(Actor subject) {
		return (actor.getActor(subject).isActive() && !actor.getActor(subject).isInCombat() && subject.game().data().getPlayer().canSee(actor.getActor(subject))) != invert;
	}

}
