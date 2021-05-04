package personal.finley.adventure_engine.actor;

import personal.finley.adventure_engine.action.Action;

/*
 * This interface allows a standardized way for an actor to choose actions. This allows swapping out multiple types of AI interchangeably, as well as player selection menus.
 */

public interface Controller {

	public Action chooseAction(Actor actor);
	
}
