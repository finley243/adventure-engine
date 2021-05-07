package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionSit;
import com.github.finley243.adventureengine.action.ActionStopUsing;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.Usable;

public class ObjectChair extends WorldObject implements Usable {

    private Actor user;

    public ObjectChair(String areaID, String name) {
        super(areaID, name);
    }

	@Override
	public boolean hasUser() {
		return user != null;
	}

	@Override
	public Actor getUser() {
		return user;
	}

	@Override
	public void setUser(Actor user) {
		this.user = user;
	}
	
	@Override
	public void removeUser() {
		this.user = null;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		if(!hasUser() && !subject.isUsingObject()) {
			actions.add(new ActionSit(this));
		}
		return actions;
	}

	@Override
	public List<Action> usingActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionStopUsing(this, "Stand up"));
		return actions;
	}
    
}
