package personal.finley.adventure_engine.actor;

import personal.finley.adventure_engine.action.IAction;
import personal.finley.adventure_engine.menu.Menu;

public class ControllerPlayer implements IController {
	
	public ControllerPlayer() {}
	
	@Override
	public IAction chooseAction(Actor actor) {
		IAction chosenAction = Menu.buildMenu(actor.availableActions());
		return chosenAction;
	}

}
