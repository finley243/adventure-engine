package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.menu.Menu;

public class ControllerPlayer implements Controller {
	
	public ControllerPlayer() {}
	
	@Override
	public Action chooseAction(Actor actor) {
		Action chosenAction = Menu.buildMenu(actor.availableActions());
		return chosenAction;
	}

}
