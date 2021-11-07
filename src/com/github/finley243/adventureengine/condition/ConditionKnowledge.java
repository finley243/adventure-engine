package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ConditionKnowledge implements Condition {

	private final String knowledgeID;
	private final boolean invert;
	
	public ConditionKnowledge(String knowledgeID, boolean invert) {
		this.knowledgeID = knowledgeID;
		this.invert = invert;
	}

	@Override
	public boolean isMet(Actor subject) {
		return invert == Data.hasKnowledge(knowledgeID);
	}

	@Override
	public String getChoiceTag() {
		return null;
	}
	
}
