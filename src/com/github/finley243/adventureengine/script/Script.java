package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	private final Condition condition;

	// TODO - Fix for recursive functions (would be overridden since each call is referencing the same object)
	private ScriptReturnTarget returnTarget;

	public Script(Condition condition) {
		this.condition = condition;
	}

	/**
	 * Execute the script if the conditions are met
	 * @param context Contains the contextual references (subject, target, local variables, etc.)
	 */
	public void execute(Context context, ScriptReturnTarget returnTarget) {
		this.returnTarget = returnTarget;
		if (canExecute(context)) {
			executeSuccess(context, returnTarget);
		}
	}

	/**
	 * Executed if all conditions are met when calling Script::execute
	 *
	 * @param context      Contains the contextual references (subject, target, etc.)
	 * @param returnTarget
	 */
	protected abstract void executeSuccess(Context context, ScriptReturnTarget returnTarget);

	protected boolean canExecute(Context context) {
		return condition == null || condition.isMet(context);
	}

	protected void sendReturn(ScriptReturn scriptReturn) {
		returnTarget.onScriptReturn(scriptReturn);
	}

	public record ScriptReturn(Expression value, boolean isReturn, boolean isBreak, String error) {}

}
