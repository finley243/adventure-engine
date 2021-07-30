package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;

/**
 * An action that can be executed at a given time
 */
public interface Script {

	public void execute(Actor subject, Actor target);
	
}
