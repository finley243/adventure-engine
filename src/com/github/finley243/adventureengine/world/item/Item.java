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

	private final boolean isGenerated;

	public Item(Game game, boolean isGenerated, String ID, Area area, String name, String description, Map<String, Script> scripts) {
		super(game, ID, area, name, description, scripts);
		this.isGenerated = isGenerated;
	}

	public abstract ItemTemplate getTemplate();

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
		super.loadState(saveData);
	}

	@Override
	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if(isGenerated) {
			state.add(0, new SaveData(SaveData.DataType.ITEM_INSTANCE, this.getID(), null, this.getTemplate().getID()));
		}
		return state;
	}
	
}
