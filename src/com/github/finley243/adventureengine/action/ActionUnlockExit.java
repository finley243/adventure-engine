package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.TextGenEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionUnlockExit implements Action {

	private ObjectExit exit;
	
	public ActionUnlockExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		exit.unlock();
		Context context = new Context(subject, exit, exit, Benefitting.SUBJECT, false, false);
		TextGenEvent text = new TextGenEvent(context, "unlockExit");
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		return "Unlock " + exit.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
