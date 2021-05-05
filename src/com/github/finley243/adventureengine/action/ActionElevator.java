package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuElevator;
import com.github.finley243.adventureengine.world.object.ObjectElevator;

public class ActionElevator implements Action {

	private ObjectElevator elevator;
	
	public ActionElevator(ObjectElevator elevator) {
		this.elevator = elevator;
	}
	
	@Override
	public void choose(Actor subject) {
		MenuElevator.buildMenuElevator(subject, elevator);
	}

	@Override
	public String getChoiceName() {
		return "Enter " + elevator.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}
