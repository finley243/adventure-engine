package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionMoveExit implements Action {

	private ObjectExit exit;
	
	public ActionMoveExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		Area area = exit.getLinkedArea();
		Context context = new Context(subject, exit, area.getRoom());
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("moveExit"), context));
		Game.EVENT_BUS.post(new VisualEvent(area, Phrases.get("moveExit"), context));
		subject.move(area);
	}

	@Override
	public String getChoiceName() {
		return "Go through " + exit.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}

}
