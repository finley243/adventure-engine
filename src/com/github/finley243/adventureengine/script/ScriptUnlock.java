package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.object.ObjectDoor;

public class ScriptUnlock extends Script {

	private final String object;
	
	public ScriptUnlock(Condition condition, String object) {
		super(condition);
		this.object = object;
	}

	@Override
	public void executeSuccess(Actor subject) {
		((ObjectDoor) subject.game().data().getObject(object)).getLock().setLocked(false);
	}

}
