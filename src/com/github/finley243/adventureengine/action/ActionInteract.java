package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.TextGenEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
import com.github.finley243.adventureengine.world.Noun;

/*
 * This action opens a sub-menu of actions (for example, a vending machine selection menu or a dialogue with an actor).
 */

public class ActionInteract implements Action {

	private Noun object;
	private String choice;
	private String text;
	
	public ActionInteract(Noun object, String choice, String text) {
		this.object = object;
		this.choice = choice;
		this.text = text;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, object, object, Benefitting.SUBJECT, false, false);
		TextGenEvent textEvent = new TextGenEvent(context, text);
		Game.EVENT_BUS.post(textEvent);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		return choice;
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
