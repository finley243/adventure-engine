package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;

public abstract class ItemEquippable extends Item {

	public ItemEquippable(String ID, String name, String description, Map<String, Script> scripts) {
		super(ID, name, description, scripts);
	}
	
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemUnequip(this));
		actions.add(new ActionItemDrop(this, true));
		return actions;
	}

}
