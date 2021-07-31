package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
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
		Context context = new Context(subject, false, elevator, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("enterElevator"), context));
		subject.move(destination.getArea());
		Game.EVENT_BUS.post(new VisualEvent(destination.getArea(), Phrases.get("exitElevator"), context));
	}

	@Override
	public String getPrompt() {
		return "Take " + elevator.getFormattedName(false) + " to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")";
	}

	@Override
	public float utility(Actor subject) {
		return 0;
	}
	
	@Override
	public int actionPoints() {
		return 1;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"Move", LangUtils.titleCase(elevator.getName())};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataWorldObject("Go to floor " + destination.getFloorNumber() + " (" + destination.getFloorName() + ")", elevator);
	}

}
