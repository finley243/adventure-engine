package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;
import com.google.errorprone.annotations.Var;

import java.util.Map;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	private final Condition condition;
	private final Map<String, Variable> localParameters;

	public Script(Condition condition, Map<String, Variable> localParameters) {
		this.condition = condition;
		this.localParameters = localParameters;
	}

	/**
	 * Execute the script if the conditions are met
	 * @param context Contains the contextual references (subject, target, etc.)
	 * @return Whether the script was executed
	 */
	public boolean execute(ContextScript context) {
		ContextScript localContext = new ContextScript(context, localParameters);
		if (canExecute(localContext)) {
			executeSuccess(localContext);
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
