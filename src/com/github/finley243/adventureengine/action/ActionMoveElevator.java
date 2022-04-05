package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectElevator;

public class ActionMoveElevator extends ActionMove {

	private final ObjectElevator elevator;
	private final ObjectElevator destination;
	
	public ActionMoveElevator(ObjectElevator elevator, ObjectElevator destination) {
		this.elevator = elevator;
		this.destination = destination;
	}
	
	public Area getArea() {
		return destination.getArea();
	}

	public ObjectElevator getElevator() {
		return elevator;
	}

	public ObjectElevator getDestination() {
		return destination;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, elevator);
		String takeElevatorPhrase;
		if(elevator.getFloorNumber() < destination.getFloorNumber()) {
			takeElevatorPhrase = "takeElevatorUp";
		} else {
			takeElevatorPhrase = "takeElevatorDown";
		}
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get(takeElevatorPhrase), context, this, subject));
		subject.game().eventBus().post(new AudioVisualEvent(destination.getArea(), Phrases.get("exitElevator"), context, this, subject));
		subject.setArea(destination.getArea());
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, destination.getArea(), true) * ActionMoveArea.MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Go to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")", canChoose(subject), new String[]{elevator.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionMoveElevator)) {
            return false;
        } else {
            ActionMoveElevator other = (ActionMoveElevator) o;
            return other.elevator == this.elevator && other.destination == this.destination;
        }
    }

}
