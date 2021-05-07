package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectVendingMachine extends WorldObject {

	private List<String> vendingItems;
	
	public ObjectVendingMachine(String name, List<String> vendingItems) {
		super(name);
		this.vendingItems = vendingItems;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		
		return actions;
	}
	
}
