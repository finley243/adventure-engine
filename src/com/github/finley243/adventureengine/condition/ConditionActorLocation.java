package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionActorLocation implements Condition {

	private final ActorReference actor;
	private final String location;
	private final boolean isRoom;
	
	public ConditionActorLocation(ActorReference actor, String location, boolean isRoom) {
		this.actor = actor;
		this.location = location;
		this.isRoom = isRoom;
	}

	@Override
	public boolean isMet(Actor subject) {
		if(isRoom) {
			return actor.getActor(subject).getArea().getRoom() == Data.getRoom(location);
		} else {
			return actor.getActor(subject).getArea() == Data.getArea(location);
		}
	}

	@Override
	public String getChoiceTag() {
		return null;
	}

}
