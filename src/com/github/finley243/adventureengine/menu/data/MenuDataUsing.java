package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.object.UsableObject;

public class MenuDataUsing extends MenuData {

	private final UsableObject usableObject;
	
	public MenuDataUsing(String prompt, UsableObject usableObject) {
		super(prompt);
		this.usableObject = usableObject;
	}
	
	public UsableObject getObject() {
		return usableObject;
	}

}
