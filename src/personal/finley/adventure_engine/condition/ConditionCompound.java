package personal.finley.adventure_engine.condition;

import java.util.List;

import personal.finley.adventure_engine.actor.Actor;

public class ConditionCompound implements Condition {
	
	private List<Condition> subconditions;
	private boolean useOr;
	
	public ConditionCompound(List<Condition> subconditions, boolean useOr) {
		this.subconditions = subconditions;
		this.useOr = useOr;
	}
	
	@Override
	public boolean isMet(Actor subject) {
		for(Condition condition : subconditions) {
			if(condition.isMet(subject) == useOr) {
				return useOr;
			}
		}
		return !useOr;
	}

}
