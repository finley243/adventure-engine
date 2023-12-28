package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

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
	 *
	 * @param runtimeStack
	 */
	public void execute(RuntimeStack runtimeStack) {
		if (canExecute(runtimeStack.getContext())) {
			executeSuccess(runtimeStack);
		}
	}

	/**
	 * Executed if all conditions are met when calling Script::execute
	 *
	 * @param runtimeStack
	 */
	protected abstract void executeSuccess(RuntimeStack runtimeStack);

	protected boolean canExecute(Context context) {
		return condition == null || condition.isMet(context);
	}

	protected void sendReturn(RuntimeStack runtimeStack, ScriptReturn scriptReturn) {
		runtimeStack.getReturnTarget().onScriptReturn(runtimeStack, scriptReturn);
	}

	public record ScriptReturn(Expression value, boolean isReturn, boolean isBreak, String error) {}

}
