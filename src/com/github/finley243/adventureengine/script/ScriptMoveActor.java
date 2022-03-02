package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.template.ItemFactory;

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
		actor.getActor(subject).move(area);
	}
	
}
