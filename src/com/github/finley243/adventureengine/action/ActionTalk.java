package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;

public class ActionTalk implements Action {

	private Actor target;
	
	public ActionTalk(Actor target) {
		this.target = target;
	}
	
	@Override
	public void choose(Actor subject) {
		if(subject instanceof ActorPlayer) {
			((ActorPlayer) subject).startDialogue(target);
		}
	}

	@Override
	public String getChoiceName() {
		return "Talk to " + target.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0;
	}

}
