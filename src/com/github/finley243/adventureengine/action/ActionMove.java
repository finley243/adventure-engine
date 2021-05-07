package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionMove implements Action {
	
	private Area area;
	
	public ActionMove(Area area) {
		this.area = area;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.move(area);
		Context context = new Context(subject, area, area);
		String line;
		if(area.isProximateName()) {
			line = "moveProx";
		} else {
			line = "move";
		}
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(line), context));
	}
	
	@Override
	public String getChoiceName() {
		return "Go toward " + area.getFormattedName();
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
