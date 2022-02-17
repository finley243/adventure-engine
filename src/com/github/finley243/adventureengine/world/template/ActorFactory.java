package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class ActorFactory {
	
	public static Actor create(String ID, Area area, StatsActor stats, String descriptor, List<String> idle, boolean preventMovement, boolean startDead, boolean startDisabled) {
		Actor actor = new Actor(ID, area, stats, descriptor, idle, preventMovement, startDead, startDisabled);
		return actor;
	}
	
	public static Actor createPlayer(String ID, Area area, StatsActor stats) {
		Actor actor = new ActorPlayer(ID, area, stats);
		return actor;
	}
	
}
