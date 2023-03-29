package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;

/**
 * A pre-condition that can be checked
 */
public abstract class Condition {
	
	public enum Equality {
		LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, NOT_EQUAL
	}

	private final boolean invert;

	public Condition(boolean invert) {
		this.invert = invert;
	}
	
	public boolean isMet(ContextScript context) {
		return isMetInternal(context) != invert;
	}

	protected abstract boolean isMetInternal(ContextScript context);

	public static boolean equalityCheckFloat(float value1, float value2, Equality equality) {
		return switch (equality) {
			case LESS -> (value1 < value2);
			case GREATER -> (value1 > value2);
			case LESS_EQUAL -> (value1 <= value2);
			case EQUAL -> (value1 == value2);
			case NOT_EQUAL -> (value1 != value2);
			default -> (value1 >= value2);
		};
	}
	
}
