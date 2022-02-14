package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionVendingMachineBuy;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;

public class ObjectVendingMachine extends WorldObject {

	private final List<String> vendingItems;
	
	public ObjectVendingMachine(String ID, String name, String description, Map<String, Script> scripts, List<String> vendingItems) {
		super(ID, name, description, scripts);
		this.vendingItems = vendingItems;
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
