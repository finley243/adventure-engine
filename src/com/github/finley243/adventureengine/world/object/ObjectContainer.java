package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;

public class ObjectContainer extends WorldObject {

	private final Inventory inventory;
	
	public ObjectContainer(String ID, String name, String description, String lootTable) {
		super(ID, name, description);
		this.inventory = new Inventory();
		inventory.addItems(Data.getLootTable(lootTable).generateItems());
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.addAll(inventory.getExternalActions(this));
		return actions;
	}

}
