package com.github.finley243.adventureengine.condition;

import java.util.List;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionCompound implements Condition {
	
	private final List<Condition> subconditions;
	private final boolean useOr;
	
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
	
	@Override
	public String getChoiceTag() {
		return null;
	}

}
