package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionVendingMachineBuy;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectVendingMachine extends WorldObject {

	private final List<String> vendingItems;
	
	public ObjectVendingMachine(String name, String description, List<String> vendingItems) {
		super(name, description);
		this.vendingItems = vendingItems;
	}

	@Override
	public boolean isPartialObstruction() {
		return true;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		for(String item : vendingItems) {
			if(subject.getMoney() >= Data.getItem(item).getPrice()) {
				actions.add(new ActionVendingMachineBuy(this, item));
			}
		}
		return actions;
	}
	
}
