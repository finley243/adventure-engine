package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemTake;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

public abstract class Item extends WorldObject {
	
	private Inventory currentInventory;
	
	public Item(String name) {
		super(name);
	}
	
	@Override
	public String getFormattedName() {
		return (isProperName() ? "" : "a ") + getName();
	}
	
	public int getPrice() {
		return 0;
	}
	
	public void setInInventory(Inventory inventory) {
		currentInventory = inventory;
		super.setArea(null);
	}
	
	@Override
	public void setArea(Area area) {
		super.setArea(area);
		currentInventory = null;
	}
	
	public boolean isInInventory() {
		return currentInventory != null;
	}
	
	public boolean isInArea() {
		return getArea() != null;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionItemTake(this));
		return actions;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public List<Action> inventoryActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
}
