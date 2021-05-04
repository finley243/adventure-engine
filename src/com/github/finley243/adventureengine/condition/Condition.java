package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

public interface Condition {
	
	public enum Equality {
		LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, NOT_EQUAL
	}
	
	public boolean isMet(Actor subject);
	
}
