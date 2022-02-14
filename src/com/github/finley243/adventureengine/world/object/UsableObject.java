package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;

/**
 * An object that can have a single "user" (e.g. a chair)
 */
public abstract class UsableObject extends WorldObject {

	private Actor user;

	public UsableObject(String ID, String name, String description, Map<String, Script> scripts) {
		super(ID, name, description, scripts);
	}

	public abstract String getStartPhrase();

	public abstract String getStopPhrase();

	public abstract String getStartPrompt();

	public abstract String getStopPrompt();

	public abstract String getStartPromptFull();

	public abstract String getStopPromptFull();

	public boolean isAvailableToUse() {
		return user == null;
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
		return new ArrayList<>();
	}
	
}
