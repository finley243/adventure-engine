package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	private Condition condition;

	public Script(Condition condition) {
		this.condition = condition;
	}

	public boolean canExecute(Actor subject) {
		return condition == null || condition.isMet(subject);
	}

	public abstract void execute(Actor subject);
	
}
