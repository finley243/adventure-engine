package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.object.WorldObject;

public class MenuDataWorldObject extends MenuData {

	private final WorldObject object;
	
	public MenuDataWorldObject(String prompt, WorldObject object) {
		super(prompt);
		this.object = object;
	}
	
	public WorldObject getObject() {
		return object;
	}

}
