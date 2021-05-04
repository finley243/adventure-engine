package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemTake;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

public abstract class Item extends WorldObject {
	
	public Item(String ID, String areaID, String name) {
		super(ID, areaID, name);
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionItemTake(this));
		return actions;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public List<Action> inventoryActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
}
