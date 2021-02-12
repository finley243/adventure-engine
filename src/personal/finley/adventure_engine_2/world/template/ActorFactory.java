package personal.finley.adventure_engine_2.world.template;

import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.actor.ControllerPlayer;
import personal.finley.adventure_engine_2.actor.ControllerUtility;
import personal.finley.adventure_engine_2.actor.IController;

public class ActorFactory {

	private static final IController NPC_CONTROLLER = new ControllerUtility();
	private static final IController PLAYER_CONTROLLER = new ControllerPlayer();
	
	public static Actor create(String ID, String areaID, TemplateActor template, boolean isPlayer) {
		IController controller;
		if(isPlayer) {
			controller = PLAYER_CONTROLLER;
		} else {
			controller = NPC_CONTROLLER;
		}
		Actor actor = new Actor(ID, areaID, template, null, null, false, controller);
		return actor;
	}
	
}
