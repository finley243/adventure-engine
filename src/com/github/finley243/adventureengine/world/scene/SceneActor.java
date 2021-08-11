package com.github.finley243.adventureengine.world.scene;

import com.github.finley243.adventureengine.actor.Actor;

public class SceneActor {

	private Actor actor;
	
	public SceneActor(Actor actor) {
		this.actor = actor;
	}
	
	public Actor getActor() {
		return actor;
	}

}
