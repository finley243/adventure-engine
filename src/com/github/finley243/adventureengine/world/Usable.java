package com.github.finley243.adventureengine.world;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

public interface Usable {

	public boolean hasUser();
	
	public Actor getUser();
	
	public void setUser(Actor user);
	
	public void removeUser();
	
	public List<Action> usingActions();
	
}
