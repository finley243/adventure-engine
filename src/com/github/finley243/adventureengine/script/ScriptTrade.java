package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptTrade extends Script {
	
	public ScriptTrade(Condition condition) {
		super(condition);
	}

	@Override
	public void execute(Actor subject) {
		if(canExecute(subject)) {

		}
	}

}
