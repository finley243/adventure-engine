package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.Usable;

public class ActionStopUsing implements Action {

	private Usable object;
	private String prompt;
	
	public ActionStopUsing(Usable object, String prompt) {
		this.object = object;
		this.prompt = prompt;
	}
	
	@Override
	public void choose(Actor subject) {
		object.removeUser();
		subject.stopUsingObject();
	}

	@Override
	public String getChoiceName() {
		return prompt;
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}
