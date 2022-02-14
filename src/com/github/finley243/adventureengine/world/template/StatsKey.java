package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.script.Script;

import java.util.Map;

public class StatsKey extends StatsItem {

	public StatsKey(String ID, String name, String description, Map<String, Script> scripts) {
		super(ID, name, description, scripts, 0);
	}

}
