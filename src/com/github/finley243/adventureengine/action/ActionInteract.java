package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;

/*
 * This action opens a sub-menu of actions (for example, a vending machine selection menu or a dialogue with an actor).
 */

public class ActionInteract implements Action {

	private Noun object;
	private String choice;
	private String line;
	
	public ActionInteract(Noun object, String choice, String line) {
		this.object = object;
		this.choice = choice;
		this.line = line;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, object, object);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(line), context));
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
