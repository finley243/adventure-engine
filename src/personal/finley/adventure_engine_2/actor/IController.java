package personal.finley.adventure_engine_2.actor;

import personal.finley.adventure_engine_2.action.IAction;

/*
 * This interface allows a standardized way for an actor to choose actions. This allows swapping out multiple types of AI interchangeably, as well as player selection menus.
 */

public interface IController {

	public IAction chooseAction(Actor actor);
	
}
