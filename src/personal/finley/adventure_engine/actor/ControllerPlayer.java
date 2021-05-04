package personal.finley.adventure_engine.actor;

import personal.finley.adventure_engine.action.Action;
import personal.finley.adventure_engine.menu.Menu;

public class ControllerPlayer implements Controller {
	
	public ControllerPlayer() {}
	
	@Override
	public Action chooseAction(Actor actor) {
		Action chosenAction = Menu.buildMenu(actor.availableActions());
		return chosenAction;
	}

}
