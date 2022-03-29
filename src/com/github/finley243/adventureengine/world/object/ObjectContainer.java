package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.script.Script;

public class ObjectContainer extends WorldObject {

	private final Inventory inventory;
	private final String lootTable;

	public ObjectContainer(Game game, String ID, String name, String description, Map<String, Script> scripts, String lootTable) {
		super(game, ID, name, description, scripts);
		this.inventory = new Inventory();
		this.lootTable = lootTable;
	}

	public void newGameInit() {
		if(lootTable != null) {
			inventory.addItems(game().data().getLootTable(lootTable).generateItems(game()));
		}
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.addAll(inventory.getExternalActions(this, subject));
		return actions;
	}

}
