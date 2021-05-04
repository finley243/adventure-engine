package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Controller;
import com.github.finley243.adventureengine.actor.ControllerPlayer;
import com.github.finley243.adventureengine.actor.ControllerUtility;

public class ActorFactory {

	private static final Controller NPC_CONTROLLER = new ControllerUtility();
	private static final Controller PLAYER_CONTROLLER = new ControllerPlayer();
	
	public static Actor create(String ID, String areaID, StatsActor stats, String topicID) {
		Actor actor = new Actor(ID, areaID, stats, topicID, false, NPC_CONTROLLER);
		return actor;
	}
	
	public static Actor createPlayer(String ID, String areaID, StatsActor stats) {
		Actor actor = new Actor(ID, areaID, stats, null, false, PLAYER_CONTROLLER);
		return actor;
	}
	
}
