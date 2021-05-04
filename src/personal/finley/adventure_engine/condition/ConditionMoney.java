package personal.finley.adventure_engine.condition;

import personal.finley.adventure_engine.actor.Actor;

public class ConditionMoney implements Condition {

	private int value;
	private Equality equality;

	public ConditionMoney(int value, Equality equality) {
		this.value = value;
		this.equality = equality;
	}

	@Override
	public boolean isMet(Actor subject) {
		switch (equality) {
		case LESS:
			return subject.getMoney() < value;
		case GREATER:
			return subject.getMoney() > value;
		case LESS_EQUAL:
			return subject.getMoney() <= value;
		case GREATER_EQUAL:
			return subject.getMoney() >= value;
		case EQUAL:
			return subject.getMoney() == value;
		case NOT_EQUAL:
			return subject.getMoney() != value;
		}
		return false;
	}

}
