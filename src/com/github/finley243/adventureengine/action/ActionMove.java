package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionMove implements Action {
	
	private Area area;
	
	public ActionMove(Area area) {
		this.area = area;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.move(area);
		Context context = new Context(subject, area, area, Benefitting.SUBJECT, false, false);
		TextEvent text;
		if(area.isProximateName()) {
			text = new TextEvent(context, "moveProx");
		} else {
			text = new TextEvent(context, "move");
		}
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
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
