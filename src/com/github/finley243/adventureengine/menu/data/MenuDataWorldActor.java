package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.actor.Actor;

public class MenuDataWorldActor extends MenuData {

	private final Actor actor;
	
	public MenuDataWorldActor(String prompt, String fullPrompt, boolean enabled, Actor actor) {
		super(prompt, fullPrompt, enabled);
		this.actor = actor;
	}
	
	public Actor getActor() {
		return actor;
	}

}
