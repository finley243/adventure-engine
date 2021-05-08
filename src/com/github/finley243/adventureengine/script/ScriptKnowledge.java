package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ScriptKnowledge implements Script {

	private String knowledgeID;
	
	public ScriptKnowledge(String knowledgeID) {
		this.knowledgeID = knowledgeID;
	}
	
	@Override
	public void execute(Actor target) {
		Data.addKnowledge(knowledgeID);
	}

}
