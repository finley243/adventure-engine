package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptExecutionException;
import com.github.finley243.adventureengine.script.ScriptRuntime;

/**
 * A pre-condition that can be checked
 */
public class Condition {

	private final Script expression;

	public Condition(Script expression) {
		if (expression == null) throw new IllegalArgumentException("Condition expression is null");
		this.expression = expression;
	}
	
	public boolean isMet(ScriptRuntime scriptRuntime, Context context) {
		Expression conditionValue = expression.run(scriptRuntime, context);
		if (conditionValue == null) {
			throw new ScriptExecutionException("Condition expression returned a null value");
		} else if (conditionValue.getDataType() != Expression.DataType.BOOLEAN) {
			throw new IllegalArgumentException("Condition expression returned a non-boolean value");
		}
		return conditionValue.getValueBoolean();
	}

}
