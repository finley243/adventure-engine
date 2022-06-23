package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class ActorFactory {
	
	public static Actor create(Game gameInstance, String ID, Area area, ActorTemplate stats, List<Behavior> behaviors, boolean startDead, boolean startDisabled) {
		return new Actor(gameInstance, ID, area, stats, behaviors, startDead, startDisabled, false);
	}
	
	public static Actor createPlayer(Game gameInstance, String ID, Area area, ActorTemplate stats) {
		return new Actor(gameInstance, ID, area, stats, null, false, false, true);
	}
	
}
