package com.github.finley243.adventureengine.actor;

import java.util.HashSet;
import java.util.Set;

import com.github.finley243.adventureengine.world.item.Item;

public class Inventory {

	private Set<Item> inventory;
	
	public Inventory() {
		inventory = new HashSet<Item>();
	}
	
	public void addItem(Item item) {
		inventory.add(item);
	}
	
	/*public boolean hasItem(Item item) {
		return inventory.contains(item);
	}*/
	
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
	
}
