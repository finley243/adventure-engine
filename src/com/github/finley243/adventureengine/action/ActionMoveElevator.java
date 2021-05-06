package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
import com.github.finley243.adventureengine.world.object.ObjectElevator;

public class ActionMoveElevator implements Action {

	private ObjectElevator elevator;
	private ObjectElevator destination;
	
	public ActionMoveElevator(ObjectElevator elevator, ObjectElevator destination) {
		this.elevator = elevator;
		this.destination = destination;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.move(destination.getArea());
		Context context = new Context(subject, elevator, Benefitting.SUBJECT, false, false);
		TextEvent text = new TextEvent(context, "moveElevator");
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		return "Take " + elevator.getFormattedName() + " to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")";
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}
