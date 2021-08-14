package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionUnequip;
import com.github.finley243.adventureengine.actor.Actor;

public abstract class ItemEquippable extends Item {

	public ItemEquippable(String name) {
		super(name);
	}
	
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionUnequip(this));
		actions.add(new ActionItemDrop(this, true));
		return actions;
	}

}
