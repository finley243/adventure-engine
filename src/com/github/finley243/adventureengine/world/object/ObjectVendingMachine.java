package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionVendingMachineBuy;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.params.ComponentParams;

import java.util.List;
import java.util.Map;

public class ObjectVendingMachine extends WorldObject {

	private final List<String> vendingItems;
	
	public ObjectVendingMachine(Game game, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, ComponentParams> componentParams, List<String> vendingItems) {
		super(game, ID, templateID, area, startDisabled, startHidden, componentParams);
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
