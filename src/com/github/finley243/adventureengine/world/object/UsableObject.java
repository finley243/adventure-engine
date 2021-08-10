package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

/**
 * An object that can have a single "user" (e.g. a chair)
 */
public abstract class UsableObject extends WorldObject {

	private Actor user;
	
	public UsableObject(String name, String description) {
		super(name, description);
	}
	
	public boolean hasUser() {
		return user != null;
	}

	public Actor getUser() {
		return user;
	}

	public void setUser(Actor user) {
		this.user = user;
	}
	
	public void removeUser() {
		this.user = null;
	}
	
	public List<Action> usingActions() {
		return new ArrayList<Action>();
	}
	
}
