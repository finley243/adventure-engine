package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ConditionKnowledge implements Condition {

	private String knowledgeID;
	private boolean invert;
	
	public ConditionKnowledge(String knowledgeID, boolean invert) {
		this.knowledgeID = knowledgeID;
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
