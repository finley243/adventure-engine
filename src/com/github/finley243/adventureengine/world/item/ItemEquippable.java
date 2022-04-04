package com.github.finley243.adventureengine.world.item;

import java.util.*;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemEquip;
import com.github.finley243.adventureengine.action.ActionItemUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public abstract class ItemEquippable extends Item {

	public ItemEquippable(Game game, boolean isGenerated, String ID, Area area, String name, String description, Map<String, Script> scripts) {
		super(game, isGenerated, ID, area, name, description, scripts);
	}

	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		actions.add(new ActionItemEquip(this));
		return actions;
	}
	
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemUnequip(this));
		return actions;
	}

}
