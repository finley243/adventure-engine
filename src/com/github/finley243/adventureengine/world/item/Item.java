package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspect;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemTake;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

public abstract class Item extends WorldObject {
	
	public Item(String name) {
		super(name, null);
	}
	
	public int getPrice() {
		return 0;
	}
	
	public abstract String getID();
	
	@Override
	public abstract String getDescription();
	
	@Override
	public void setArea(Area area) {
		super.setArea(area);
	}
	
	public abstract boolean equalsInventory(Item other);

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.add(new ActionItemTake(this));
		return actions;
	}
	
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemDrop(this, false));
		if(this.getDescription() != null) {
			actions.add(new ActionInspect(this, InspectType.INVENTORY));
		}
		return actions;
	}
	
}
