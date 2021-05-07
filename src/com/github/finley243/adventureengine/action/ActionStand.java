package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionStand implements Action {

	private UsableObject object;
	
	public ActionStand(UsableObject object) {
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject) {
		object.removeUser();
		subject.stopUsingObject();
		Context context = new Context(subject, object);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("stand"), context));
	}

	@Override
	public String getChoiceName() {
		return "Stand up";
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}
