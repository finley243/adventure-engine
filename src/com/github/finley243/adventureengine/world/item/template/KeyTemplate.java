package com.github.finley243.adventureengine.world.item.template;

import com.github.finley243.adventureengine.script.Script;

import java.util.Map;

public class KeyTemplate extends ItemTemplate {

	public KeyTemplate(String ID, String name, String description, Map<String, Script> scripts) {
		super(ID, name, description, scripts, 0);
	}

}
