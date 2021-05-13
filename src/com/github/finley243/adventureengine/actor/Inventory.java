package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInventoryTake;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.item.Item;

public class Inventory {

	private Set<Item> inventory;
	
	public Inventory() {
		inventory = new HashSet<Item>();
	}
	
	public void addItem(Item item) {
		inventory.add(item);
	}
	
	public boolean hasItemWithID(String ID) {
		for(Item item : inventory) {
			if(item.getID().equals(ID)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeItem(Item item) {
		inventory.remove(item);
	}
	
	public Set<Item> getItems() {
		return inventory;
	}
	
	public Set<Item> getUniqueItems() {
		Set<Item> uniqueItems = new HashSet<Item>();
		for(Item item : inventory) {
			boolean hasMatch = false;
			for(Item uniqueItem : uniqueItems) {
				if(item.equalsInventory(uniqueItem)) {
					hasMatch = true;
				}
			}
			if(!hasMatch) {
				uniqueItems.add(item);
			}
		}
		return uniqueItems;
	}

	public List<Action> getActions(Noun owner) {
		List<Action> actions = new ArrayList<Action>();
		for(Item item : inventory) {
			actions.add(new ActionInventoryTake(owner, this, item));
		}
		return actions;
	}
	
}
