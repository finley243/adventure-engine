package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.actor.Actor;

public class MenuDataWorldActor extends MenuData {

	private final Actor actor;
	
	public MenuDataWorldActor(String prompt, boolean enabled, Actor actor) {
		super(prompt, enabled);
		this.actor = actor;
	}
	
	public Actor getActor() {
		return actor;
	}

}
