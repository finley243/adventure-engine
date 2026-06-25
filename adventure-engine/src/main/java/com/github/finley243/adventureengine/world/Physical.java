package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

/**
 * Represents an object that is located in the game world
 */
public interface Physical {
	
	Area getArea();
	
	void setArea(Area area);
	
	// Actions that can be performed within the same area
	List<Action> localActions(Actor subject, ActionDependencies dependencies);

	// Actions that can be performed whenever the target can be seen
	List<Action> visibleActions(Actor subject, ActionDependencies dependencies);
	
}
