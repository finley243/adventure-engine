package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

public abstract class UsableObject extends WorldObject {

	private Actor user;
	
	public UsableObject(String name) {
		super(name);
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
