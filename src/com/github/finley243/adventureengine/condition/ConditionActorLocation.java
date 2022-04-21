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
	public boolean isMet(Actor subject) {
		if(isRoom) {
			return (actor.getActor(subject).getArea().getRoom() == subject.game().data().getRoom(location)) != invert;
		} else {
			return (actor.getActor(subject).getArea() == subject.game().data().getArea(location)) != invert;
		}
	}

}
