package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
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
	 * @param context Contains the contextual references (subject, target, local variables, etc.)
	 */
	public void execute(Context context) {
		if (canExecute(context)) {
			executeSuccess(context);
		}
	}

	/**
	 * Executed if all conditions are met when calling Script::execute
	 *
	 * @param context Contains the contextual references (subject, target, etc.)
	 */
	protected abstract void executeSuccess(Context context);

	protected boolean canExecute(Context context) {
		return condition == null || condition.isMet(context);
	}

}
