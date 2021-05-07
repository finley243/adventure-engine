package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionWait implements Action {

	public ActionWait() {
		
	}

	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("wait"), context));
	}

	@Override
	public String getChoiceName() {
		return "Wait";
	}

	@Override
	public float utility(Actor subject) {
		return 0.00001f;
	}
	
}
