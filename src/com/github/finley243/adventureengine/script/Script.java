package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	/**
	 * Begin execution of the script
	 *
	 * @param runtimeStack
	 */
	public abstract void execute(RuntimeStack runtimeStack);

	protected void sendReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
		runtimeStack.getReturnTarget().onScriptReturn(runtimeStack, scriptReturnData);
	}

	public record ScriptReturnData(Expression value, boolean isReturn, boolean isBreak, String error) {}

}
