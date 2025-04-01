package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

/**
 * A pre-condition that can be checked
 */
public class Condition {

	private final Script expression;

	public Condition(Script expression) {
		if (expression == null) throw new IllegalArgumentException("Condition expression is null");
		this.expression = expression;
	}
	
	public boolean isMet(Context context) {
		Script.ScriptReturnData result = expression.execute(context);
		// TODO - Replace exceptions with error log and default to false
		if (result.error() != null) {
			throw new IllegalArgumentException("Condition expression encountered an error during execution: " + result.stackTrace());
		} else if (result.flowStatement() != null) {
			throw new IllegalArgumentException("Condition expression contains an unexpected flow statement");
		} else if (result.value() == null) {
			throw new IllegalArgumentException("Condition expression did not return a value");
		} else if (result.value().getDataType() != Expression.DataType.BOOLEAN) {
			throw new IllegalArgumentException("Condition expression did not return a boolean value");
		}
		return result.value().getValueBoolean();
	}

}
