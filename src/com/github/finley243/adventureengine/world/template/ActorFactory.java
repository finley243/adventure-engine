package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActorFactory {
	
	public static Actor create(String ID, Area area, StatsActor stats, String descriptor) {
		Actor actor = new Actor(ID, area, stats, descriptor, false);
		return actor;
	}
	
	public static Actor createPlayer(String ID, Area area, StatsActor stats) {
		Actor actor = new ActorPlayer(ID, area, stats);
		return actor;
	}
	
}
