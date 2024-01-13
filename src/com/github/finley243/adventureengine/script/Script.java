package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	/**
	 * Begin execution of the script
	 *
	 * @param context
	 */
	public abstract ScriptReturnData execute(Context context);

	/*protected void sendReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
		runtimeStack.getReturnTarget().onScriptReturn(runtimeStack, scriptReturnData);
	}*/

	public record ScriptReturnData(Expression value, boolean isReturn, boolean isBreak, String error) {}

	public static Script constant(boolean value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(int value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(float value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(String value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(Set<String> value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(Inventory value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(Noun value) {
		return new ScriptExpression(Expression.constant(value));
	}

}
