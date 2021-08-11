package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ScriptUnlock implements Script {

	private String object;
	
	public ScriptUnlock(String object) {
		this.object = object;
	}

	@Override
	public void execute(Actor subject) {
		((ObjectExit) Data.getLinkedObject(object)).unlock();
	}

}
