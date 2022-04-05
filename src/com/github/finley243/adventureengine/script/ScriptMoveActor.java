package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;

public class ScriptMoveActor extends Script {

	private final ActorReference actor;
	private final String areaID;

	public ScriptMoveActor(Condition condition, ActorReference actor, String areaID) {
		super(condition);
		this.actor = actor;
		this.areaID = areaID;
	}
	
	@Override
	public void executeSuccess(Actor subject) {
		Area area = subject.game().data().getArea(areaID);
		actor.getActor(subject).setArea(area);
	}
	
}
