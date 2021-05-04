package personal.finley.adventure_engine.condition;

import personal.finley.adventure_engine.actor.Actor;

public interface Condition {
	
	public enum Equality {
		LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, NOT_EQUAL
	}
	
	public boolean isMet(Actor subject);
	
}
