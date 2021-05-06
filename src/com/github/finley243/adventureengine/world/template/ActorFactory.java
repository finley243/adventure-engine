package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;

public class ActorFactory {
	
	public static Actor create(String ID, String areaID, StatsActor stats, String topicID) {
		Actor actor = new Actor(ID, areaID, stats, topicID, false);
		return actor;
	}
	
	public static Actor createPlayer(String ID, String areaID, StatsActor stats) {
		Actor actor = new ActorPlayer(ID, areaID, stats);
		return actor;
	}
	
}
