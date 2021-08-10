package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionSit;
import com.github.finley243.adventureengine.action.ActionStand;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectChair extends UsableObject {

    public ObjectChair(String name, String description) {
        super(name, description);
    }
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if(!hasUser() && !subject.isUsingObject()) {
			actions.add(new ActionSit(this));
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
