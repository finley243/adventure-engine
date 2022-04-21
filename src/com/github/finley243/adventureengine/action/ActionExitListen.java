package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.object.ObjectDoor;

public class ActionExitListen extends Action {

	private final ObjectDoor exit;
	
	public ActionExitListen(ObjectDoor exit) {
		this.exit = exit;
	}

	@Override
	public void choose(Actor subject) {
		String text = "";
		int actorCount = 0;
		for (Actor actor : exit.getLinkedArea().getRoom().getActors()) {
			if (actor.isActive()) {
				actorCount++;
			}
		}
		if (actorCount == 0) {
			text += "You don't hear anyone";
		} else if (actorCount > 5) {
			text += "You hear more than 5 people";
		} else if(actorCount == 1) {
			text += "You hear 1 person";
		} else {
			text += "You hear " + actorCount + " people";
		}
		text += " through " + exit.getFormattedName() + ".";
		subject.game().eventBus().post(new RenderTextEvent(text));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Listen", canChoose(subject), new String[]{exit.getName()});
	}

}
