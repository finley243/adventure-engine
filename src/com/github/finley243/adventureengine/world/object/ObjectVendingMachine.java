package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionVendingMachineBuy;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectVendingMachine extends WorldObject {

	private final List<String> vendingItems;
	
	public ObjectVendingMachine(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts, List<String> vendingItems) {
		super(game, ID, area, name, description, scripts);
		this.vendingItems = vendingItems;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		for(String item : vendingItems) {
			if(subject.getMoney() >= game().data().getItem(item).getPrice()) {
				actions.add(new ActionVendingMachineBuy(this, item));
			}
		}
		return actions;
	}
	
}
