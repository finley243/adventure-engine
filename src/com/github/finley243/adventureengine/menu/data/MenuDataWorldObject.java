package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.object.WorldObject;

public class MenuDataWorldObject extends MenuData {

	private final WorldObject object;
	
	public MenuDataWorldObject(String prompt, String fullPrompt, boolean enabled, WorldObject object) {
		super(prompt, fullPrompt, enabled);
		this.object = object;
	}
	
	public WorldObject getObject() {
		return object;
	}

}
