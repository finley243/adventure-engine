package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.object.UsableObject;

public class MenuDataUsing extends MenuData {

	private final UsableObject usableObject;
	
	public MenuDataUsing(String prompt, String fullPrompt, boolean enabled, UsableObject usableObject) {
		super(prompt, fullPrompt, enabled);
		this.usableObject = usableObject;
	}
	
	public UsableObject getObject() {
		return usableObject;
	}

}
