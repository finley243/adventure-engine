package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	private final Condition condition;

	public Script(Condition condition) {
		this.condition = condition;
	}

	/**
	 * Execute the script if the conditions are met
	 * @param subject The contextual subject actor
	 * @return Whether the script was executed
	 */
	public boolean execute(Actor subject, Actor target) {
		if(canExecute(subject, target)) {
			executeSuccess(subject, target);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Executed if all conditions are met when calling Script::execute
	 *
	 * @param subject The contextual subject actor
	 * @param target The contextual target actor
	 */
	protected abstract void executeSuccess(Actor subject, Actor target);

	private boolean canExecute(Actor subject, Actor target) {
		return condition == null || condition.isMet(subject, target);
	}

}
