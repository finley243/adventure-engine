package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.ExpressionCompare;

/**
 * A pre-condition that can be checked
 */
public abstract class Condition {

	private final boolean invert;

	public Condition(boolean invert) {
		this.invert = invert;
	}
	
	public boolean isMet(Context context) {
		return isMetInternal(context) != invert;
	}

	protected abstract boolean isMetInternal(Context context);

	public static boolean comparatorCheckFloat(float value1, float value2, ExpressionCompare.Comparator comparator) {
		return switch (comparator) {
			case LESS -> (value1 < value2);
			case GREATER -> (value1 > value2);
			case LESS_EQUAL -> (value1 <= value2);
			case EQUAL -> (value1 == value2);
			case NOT_EQUAL -> (value1 != value2);
			default -> (value1 >= value2);
		};
	}
	
}
