package com.github.finley243.adventureengine.actor;

import java.util.*;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInventoryTake;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.item.Item;

public class Inventory {

	private final Map<String, List<Item>> items;
	
	public Inventory() {
		items = new HashMap<>();
	}
	
	public void addItem(Item item) {
		if(!items.containsKey(item.getStatsID())) {
			items.put(item.getStatsID(), new ArrayList<>());
		}
		items.get(item.getStatsID()).add(item);
	}
	
	public void addItems(Set<Item> itemSet) {
		for(Item item : itemSet) {
			addItem(item);
		}
	}
	
	public boolean hasItemWithID(String ID) {
		return items.containsKey(ID);
	}

	public int itemCountWithID(String ID) {
		if(!items.containsKey(ID)) return 0;
		return items.get(ID).size();
	}
	
	public void removeItem(Item item) {
		if(items.containsKey(item.getStatsID())) {
			List<Item> itemList = items.get(item.getStatsID());
			itemList.remove(0);
			if(itemList.isEmpty()) {
				items.remove(item.getStatsID());
			}
		}
	}
	
	public void clear() {
		items.clear();
	}
	
	public Set<Item> getUniqueItems() {
		Set<Item> uniqueItems = new HashSet<>();
		for(List<Item> current : items.values()) {
			uniqueItems.add(current.get(0));
		}
		return uniqueItems;
	}

	public List<Action> getExternalActions(Noun owner) {
		List<Action> actions = new ArrayList<>();
		for(List<Item> current : items.values()) {
			actions.add(new ActionInventoryTake(owner, this, current.get(0)));
		}
		return actions;
	}
	
}
