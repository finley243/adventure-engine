package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.environment.Area;

public class MenuDataMove extends MenuData {

	private final Area area;
	
	public MenuDataMove(String prompt, String fullPrompt, boolean enabled, Area area) {
		super(prompt, fullPrompt, enabled);
		this.area = area;
	}
	
	public Area getArea() {
		return area;
	}

}
