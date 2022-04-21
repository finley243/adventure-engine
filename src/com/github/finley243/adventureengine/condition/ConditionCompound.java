package com.github.finley243.adventureengine.condition;

import java.util.List;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionCompound extends Condition {
	
	private final List<Condition> conditions;
	private final boolean useOr;
	
	public ConditionCompound(boolean invert, List<Condition> conditions, boolean useOr) {
		super(invert);
		this.conditions = conditions;
		this.useOr = useOr;
	}
	
	@Override
	public boolean isMet(Actor subject) {
		for(Condition condition : conditions) {
			if(condition.isMet(subject) == useOr) {
				return useOr != invert;
			}
		}
		return useOr == invert;
	}

}
