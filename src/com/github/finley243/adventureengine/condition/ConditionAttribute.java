package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.actor.Actor.Attribute;

public class ConditionAttribute extends Condition {

	private final ActorReference actor;
	private final Attribute attribute;
	private final int value;
	
	public ConditionAttribute(boolean invert, ActorReference actor, Attribute attribute, int value) {
		super(invert);
		this.actor = actor;
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public boolean isMet(Actor subject) {
		return (actor.getActor(subject).getAttribute(attribute) >= value) != invert;
	}

}
