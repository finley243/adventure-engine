package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

/**
 * A pre-condition that can be checked
 */
public abstract class Condition {
	
	public enum Equality {
		LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, NOT_EQUAL
	}

	protected final boolean invert;

	public Condition(boolean invert) {
		this.invert = invert;
	}
	
	public abstract boolean isMet(Actor subject);

	public static boolean equalityCheckInt(int value1, int value2, Equality equality, boolean invert) {
		switch(equality) {
			case LESS:
				return (value1 < value2) != invert;
			case GREATER:
				return (value1 > value2) != invert;
			case LESS_EQUAL:
				return (value1 <= value2) != invert;
			case EQUAL:
				return (value1 == value2) != invert;
			case NOT_EQUAL:
				return (value1 == value2) == invert;
			case GREATER_EQUAL:
			default:
				return (value1 >= value2) != invert;
		}
	}

	public static boolean equalityCheckFloat(float value1, float value2, Equality equality, boolean invert) {
		switch(equality) {
			case LESS:
				return (value1 < value2) != invert;
			case GREATER:
				return (value1 > value2) != invert;
			case LESS_EQUAL:
				return (value1 <= value2) != invert;
			case EQUAL:
				return (value1 == value2) != invert;
			case NOT_EQUAL:
				return (value1 == value2) == invert;
			case GREATER_EQUAL:
			default:
				return (value1 >= value2) != invert;
		}
	}
	
}
