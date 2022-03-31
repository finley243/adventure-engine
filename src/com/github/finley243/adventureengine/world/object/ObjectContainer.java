package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;

public class ObjectContainer extends WorldObject {

	private final Inventory inventory;
	private final String lootTable;

	public ObjectContainer(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts, String lootTable) {
		super(game, ID, area, name, description, scripts);
		this.inventory = new Inventory(null);
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

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "inventory":
				inventory.addItem((Item) game().data().getObject(saveData.getValueString()));
				break;
			default:
				super.loadState(saveData);
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		for(Item item : inventory.getAllItems()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "inventory", item.getID()));
		}
		return state;
	}

}
