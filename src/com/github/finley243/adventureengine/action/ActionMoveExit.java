package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
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
		subject.move(area);
		Context context = new Context(subject, exit, area.getRoom(), Benefitting.SUBJECT, false, false);
		TextEvent text;
		text = new TextEvent(context, "moveExit");
		/*
		if(area.isProximateName()) {
			text = new TextEvent(context, "moveProx");
		} else {
			text = new TextEvent(context, "move");
		}
		*/
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
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
