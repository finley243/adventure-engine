package com.github.finley243.adventureengine.condition;

import java.util.List;

import com.github.finley243.adventureengine.ContextScript;

public class ConditionCompound extends Condition {
	
	private final List<Condition> conditions;
	private final boolean useOr;
	
	public ConditionCompound(boolean invert, List<Condition> conditions, boolean useOr) {
		super(invert);
		this.conditions = conditions;
		this.useOr = useOr;
	}
	
	@Override
	public boolean isMetInternal(ContextScript context) {
		for(Condition condition : conditions) {
			if(condition.isMet(context) == useOr) {
				return useOr;
			}
		}
		return !useOr;
	}

}
