package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionContainerSearch;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;
import java.util.Map;

public class ObjectContainer extends WorldObject {

	private final Inventory inventory;
	private final LootTable lootTable;
	private boolean hasSearched;

	public ObjectContainer(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts, LootTable lootTable) {
		super(game, ID, area, name, description, scripts);
		this.inventory = new Inventory(game, null);
		this.lootTable = lootTable;
		this.hasSearched = false;
	}

	public void newGameInit() {
		if(lootTable != null) {
			inventory.addItems(lootTable.generateItems(game()));
		}
	}

	public void search() {
		hasSearched = true;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if (hasSearched) {
			actions.addAll(inventory.getExternalActions(this, subject));
		} else {
			actions.add(new ActionContainerSearch(this));
		}
		return actions;
	}

	public void loadState(SaveData saveData) {
		if (saveData.getParameter().equals("inventory")) {
			if (inventory != null) inventory.loadState(saveData);
		} else {
			super.loadState(saveData);
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if (inventory != null) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "inventory", inventory.saveState()));
		}
		return state;
	}

}
