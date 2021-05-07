package com.github.finley243.adventureengine.world;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public interface Physical {
	
	public Area getArea();
	
	public void setArea(Area area);
	
	// Actions that can be performed within the same area
	public List<Action> localActions(Actor subject);
	
	// Actions that can be performed anywhere within the same room
	public List<Action> remoteActions(Actor subject);
	
}
