package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;

public class ActionWait implements Action {

	public ActionWait() {
		
	}

	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, Benefitting.SUBJECT, false, false);
		TextEvent text = new TextEvent(context, "wait");
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
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
