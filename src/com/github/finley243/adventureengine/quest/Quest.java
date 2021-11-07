package com.github.finley243.adventureengine.quest;

import java.util.Map;

public class Quest {

	private final String name;
	private final Map<String, Objective> objectives;
	
	public Quest(String name, Map<String, Objective> objectives) {
		this.name = name;
		this.objectives = objectives;
	}

	public String getName() {
		return name;
	}
	
	public Objective getObjective(String key) {
		return objectives.get(key);
	}

}
