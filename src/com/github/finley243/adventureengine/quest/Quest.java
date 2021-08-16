package com.github.finley243.adventureengine.quest;

import java.util.Map;

public class Quest {

	private Map<String, Objective> objectives;
	
	public Quest(Map<String, Objective> objectives) {
		this.objectives = objectives;
	}
	
	public Objective getObjective(String key) {
		return objectives.get(key);
	}

}
