package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

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
	
	public boolean isMet(Actor subject, Actor target) {
		return isMetInternal(subject, target) != invert;
	}

	protected abstract boolean isMetInternal(Actor subject, Actor target);

	public static boolean equalityCheckInt(int value1, int value2, Equality equality) {
		switch(equality) {
			case LESS:
				return (value1 < value2);
			case GREATER:
				return (value1 > value2);
			case LESS_EQUAL:
				return (value1 <= value2);
			case EQUAL:
				return (value1 == value2);
			case NOT_EQUAL:
				return (value1 != value2);
			case GREATER_EQUAL:
			default:
				return (value1 >= value2);
		}
	}

	public static boolean equalityCheckFloat(float value1, float value2, Equality equality) {
		switch(equality) {
			case LESS:
				return (value1 < value2);
			case GREATER:
				return (value1 > value2);
			case LESS_EQUAL:
				return (value1 <= value2);
			case EQUAL:
				return (value1 == value2);
			case NOT_EQUAL:
				return (value1 != value2);
			case GREATER_EQUAL:
			default:
				return (value1 >= value2);
		}
	}
	
}
