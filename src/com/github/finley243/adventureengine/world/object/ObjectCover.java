package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionStand;
import com.github.finley243.adventureengine.action.ActionUseCover;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectCover extends UsableObject {

	public ObjectCover(String name, String description) {
		super(name, description);
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if(isAvailableToUse() && !subject.isUsingObject()) {
			actions.add(new ActionUseCover(this));
		}
		return actions;
	}

	@Override
	public List<Action> usingActions() {
		List<Action> actions = super.usingActions();
		actions.add(new ActionStand(this));
		return actions;
	}

}
