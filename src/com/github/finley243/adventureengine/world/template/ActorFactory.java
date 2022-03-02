package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class ActorFactory {
	
	public static Actor create(Game gameInstance, String ID, Area area, StatsActor stats, String descriptor, List<String> idle, boolean preventMovement, boolean startDead, boolean startDisabled) {
		Actor actor = new Actor(gameInstance, ID, area, stats, descriptor, idle, preventMovement, startDead, startDisabled);
		return actor;
	}
	
	public static Actor createPlayer(Game gameInstance, String ID, Area area, StatsActor stats) {
		Actor actor = new ActorPlayer(gameInstance, ID, area, stats);
		return actor;
	}
	
}
