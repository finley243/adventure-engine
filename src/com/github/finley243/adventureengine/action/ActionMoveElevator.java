package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
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

	@Override
	public Area getDestinationArea() {
		return destination.getArea();
	}

	public ObjectElevator getElevator() {
		return elevator;
	}

	public ObjectElevator getDestination() {
		return destination;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Area lastArea = subject.getArea();
		Context context = new Context(new MapBuilder<String, Noun>().put("actor", subject).put("elevator", elevator).build());
		String takeElevatorPhrase;
		if(elevator.getFloorNumber() < destination.getFloorNumber()) {
			takeElevatorPhrase = "takeElevatorUp";
		} else {
			takeElevatorPhrase = "takeElevatorDown";
		}
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(takeElevatorPhrase), context, this, null, subject, null));
		subject.game().eventBus().post(new SensoryEvent(destination.getArea(), Phrases.get("exitElevator"), context, this, null, subject, null));
		subject.setArea(destination.getArea());
		subject.onMove(lastArea);
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, destination.getArea(), true) * ActionMoveArea.MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Go to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")", canChoose(subject), new String[]{elevator.getName()}, new String[]{"take " + elevator.getName() + " to " + destination.getFloorName(), "ride " + elevator.getName() + " to " + destination.getFloorName(), "take " + elevator.getName() + " to floor " + destination.getFloorNumber(), "ride " + elevator.getName() + " to floor " + destination.getFloorNumber(), "take " + elevator.getName() + " to level " + destination.getFloorNumber(), "ride " + elevator.getName() + " to level " + destination.getFloorNumber()});
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
