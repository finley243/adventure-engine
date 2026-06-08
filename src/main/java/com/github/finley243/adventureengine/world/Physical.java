package com.github.finley243.adventureengine.world;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

/**
 * Represents an object that is located in the game world
 */
public interface Physical {
	
	Area getArea();
	
	void setArea(Area area, Game game);
	
	// Actions that can be performed within the same area
	List<Action> localActions(Game game, Actor subject);

	// Actions that can be performed whenever the target can be seen
	List<Action> visibleActions(Actor subject);
	
}
