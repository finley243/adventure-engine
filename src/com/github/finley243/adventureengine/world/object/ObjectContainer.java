package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionContainerSearch;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;
import java.util.Map;

public class ObjectContainer extends WorldObject {

	private final Inventory inventory;
	private final LootTable lootTable;
	private final Lock lock;
	private final boolean isOpen;
	private boolean hasSearched;

	public ObjectContainer(Game game, String ID, Area area, String name, Scene description, boolean startDisabled, boolean startHidden, Map<String, Script> scripts, List<ActionCustom> customActions, LootTable lootTable, Lock lock, boolean isOpen) {
		super(game, ID, area, name, description, startDisabled, startHidden, scripts, customActions);
		this.inventory = new Inventory(game, null);
		this.lootTable = lootTable;
		this.lock = lock;
		this.isOpen = isOpen;
		this.hasSearched = false;
	}

	@Override
	public void newGameInit() {
		if(lootTable != null) {
			inventory.addItems(lootTable.generateItems(game()));
		}
	}

	public void search() {
		hasSearched = true;
	}

	public boolean isLocked() {
		return lock != null && lock.isLocked();
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if ((!subject.isPlayer() || isOpen || hasSearched) && !isLocked()) {
			actions.addAll(inventory.getExternalActions(this, subject, isOpen));
		} else {
			actions.add(new ActionContainerSearch(this));
		}
		if (lock != null) {
			actions.addAll(lock.getActions(subject));
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
