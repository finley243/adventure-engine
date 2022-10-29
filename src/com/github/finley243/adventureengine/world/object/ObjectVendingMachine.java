package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionVendingMachineBuy;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;
import java.util.Map;

public class ObjectVendingMachine extends WorldObject {

	private final List<String> vendingItems;
	
	public ObjectVendingMachine(Game game, String ID, Area area, String name, Scene description, boolean startDisabled, boolean startHidden, Map<String, Script> scripts, List<ActionCustom> customActions, Map<String, String> linkedObjects, List<String> vendingItems) {
		super(game, ID, area, name, description, startDisabled, startHidden, scripts, customActions, linkedObjects);
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
