package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

/**
 * A pre-condition that can be checked
 */
public class Condition {

	private final boolean invert;
	private final Expression expression;

	public Condition(boolean invert, Expression expression) {
		if (expression == null) throw new IllegalArgumentException("Condition expression is null");
		this.invert = invert;
		this.expression = expression;
	}
	
	public boolean isMet(Context context) {
		return isMetInternal(context) != invert;
	}

	private boolean isMetInternal(Context context) {
		if (expression.getDataType(context) != Expression.DataType.BOOLEAN) throw new IllegalArgumentException("Condition expression is not a boolean");
		return expression.getValueBoolean(context);
	}

}
