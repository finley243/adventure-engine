package personal.finley.adventure_engine_2.world.template;

import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.actor.ControllerPlayer;
import personal.finley.adventure_engine_2.actor.ControllerUtility;
import personal.finley.adventure_engine_2.actor.IController;

public class ActorFactory {

	private static final IController NPC_CONTROLLER = new ControllerUtility();
	private static final IController PLAYER_CONTROLLER = new ControllerPlayer();
	
	public static Actor create(String ID, String areaID, StatsActor stats, String topicID) {
		Actor actor = new Actor(ID, areaID, stats, topicID, false, NPC_CONTROLLER);
		return actor;
	}
	
	public static Actor createPlayer(String ID, String areaID, StatsActor stats) {
		Actor actor = new Actor(ID, areaID, stats, null, false, PLAYER_CONTROLLER);
		return actor;
	}
	
}
