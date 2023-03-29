package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
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
	 * @param context Contains the contextual references (subject, target, etc.)
	 * @return Whether the script was executed
	 */
	public boolean execute(ContextScript context) {
		if (canExecute(context)) {
			executeSuccess(context);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Executed if all conditions are met when calling Script::execute
	 *
	 * @param context Contains the contextual references (subject, target, etc.)
	 */
	protected abstract void executeSuccess(ContextScript context);

	private boolean canExecute(ContextScript context) {
		return condition == null || condition.isMet(context);
	}

}
