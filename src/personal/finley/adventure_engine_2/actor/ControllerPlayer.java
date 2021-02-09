package personal.finley.adventure_engine_2.actor;

import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.menu.Menu;

public class ControllerPlayer implements IController {
	
	public ControllerPlayer() {}
	
	@Override
	public IAction chooseAction(Actor actor) {
		IAction chosenAction = Menu.buildMenu(actor.availableActions());
		return chosenAction;
	}

}
