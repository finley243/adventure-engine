package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.actor.Actor.Attribute;

public class ConditionAttribute implements Condition {

	private ActorReference actor;
	private Attribute attribute;
	private int value;
	
	public ConditionAttribute(ActorReference actor, Attribute attribute, int value) {
		this.actor = actor;
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public boolean isMet(Actor subject) {
		return actor.getActor(subject).getAttribute(attribute) >= value;
	}

	@Override
	public String getChoiceTag() {
		return value + " " + attribute.toString();
	}

}
