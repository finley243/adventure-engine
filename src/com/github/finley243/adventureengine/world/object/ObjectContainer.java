package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;

public class ObjectContainer extends WorldObject {

	private Inventory inventory;
	
	public ObjectContainer(String ID, String name, String description) {
		super(ID, name, description);
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		
		return actions;
	}

}
