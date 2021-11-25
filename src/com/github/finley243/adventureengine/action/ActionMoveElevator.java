package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.CombatTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectElevator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionMoveElevator implements Action {

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
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, false, elevator, false);
		String takeElevatorPhrase;
		if(elevator.getFloorNumber() < destination.getFloorNumber()) {
			takeElevatorPhrase = "takeElevatorUp";
		} else {
			takeElevatorPhrase = "takeElevatorDown";
		}
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(takeElevatorPhrase), context, this, subject));
		Game.EVENT_BUS.post(new VisualEvent(destination.getArea(), Phrases.get("exitElevator"), context, this, subject));
		subject.move(destination.getArea());
	}

	@Override
	public boolean canChoose(Actor subject) {
		return true;
	}

	@Override
	public String getPrompt() {
		return "Take " + elevator.getFormattedName(false) + " to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")";
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, destination.getArea()) * ActionMove.MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return action instanceof ActionMove ||
			action instanceof ActionMoveExit ||
			action instanceof ActionMoveElevator;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataWorldObject("Go to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")", canChoose(subject), elevator);
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
