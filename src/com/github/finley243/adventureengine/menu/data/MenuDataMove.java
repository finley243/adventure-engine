package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.environment.Area;

public class MenuDataMove extends MenuData {

	private Area area;
	
	public MenuDataMove(String prompt, Area area) {
		super(prompt);
		this.area = area;
	}
	
	public Area getArea() {
		return area;
	}

}
