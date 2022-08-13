package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorLocation extends Condition {

	private final ActorReference actor;
	private final String location;
	private final boolean isRoom;
	
	public ConditionActorLocation(boolean invert, ActorReference actor, String location, boolean isRoom) {
		super(invert);
		this.actor = actor;
		this.location = location;
		this.isRoom = isRoom;
	}

	@Override
	public boolean isMetInternal(Actor subject, Actor target) {
		if(isRoom) {
			return (actor.getActor(subject, target).getArea().getRoom() == subject.game().data().getRoom(location));
		} else {
			return (actor.getActor(subject, target).getArea() == subject.game().data().getArea(location));
		}
	}

}
