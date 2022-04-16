package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspect;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemTake;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Item extends WorldObject {

	// Determines whether a new version of this item needs to be created when loading a save file
	private final boolean isGenerated;
	// Used to allow "stacking" of items with no state (unused by items with state)
	private int count;

	public Item(Game game, boolean isGenerated, String ID, Area area, String name, String description, Map<String, Script> scripts) {
		super(game, ID, area, name, description, scripts);
		this.isGenerated = isGenerated;
		this.count = 1;
	}

	public abstract ItemTemplate getTemplate();

	public int getCount() {
		return count;
	}

	public void addCount(int amount) {
		count += amount;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.add(new ActionItemTake(this));
		return actions;
	}
	
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemDrop(this));
		if(this.getDescription() != null) {
			actions.add(new ActionInspect(this, InspectType.INVENTORY));
		}
		return actions;
	}

	@Override
	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "count":
				this.count = saveData.getValueInt();
				break;
			default:
				super.loadState(saveData);
				break;
		}
	}

	@Override
	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if(isGenerated) {
			state.add(0, new SaveData(SaveData.DataType.ITEM_INSTANCE, this.getID(), null, this.getTemplate().getID()));
		}
		if(count > 1) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "count", count));
		}
		return state;
	}
	
}
