package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ScriptUnlock extends Script {

	private final String object;
	
	public ScriptUnlock(Condition condition, String object) {
		super(condition);
		this.object = object;
	}

	@Override
	public void execute(Actor subject) {
		((ObjectExit) Data.getObject(object)).unlock();
	}

}
