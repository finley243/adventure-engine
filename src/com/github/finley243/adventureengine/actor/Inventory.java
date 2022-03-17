package com.github.finley243.adventureengine.actor;

import java.util.*;

import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.Item;

public class Inventory {

	// Keys are statsIDs, values are lists of items with the corresponding statsID
	private final Map<String, List<Item>> items;
	
	public Inventory() {
		items = new HashMap<>();
	}

	public List<Item> getAllItems() {
		List<Item> allItems = new ArrayList<>();
		for(List<Item> itemList : items.values()) {
			allItems.addAll(itemList);
		}
		return allItems;
	}
	
	public void addItem(Item item) {
		if(!items.containsKey(item.getStatsID())) {
			items.put(item.getStatsID(), new ArrayList<>());
		}
		items.get(item.getStatsID()).add(item);
	}
	
	public void addItems(List<Item> itemList) {
		for(Item item : itemList) {
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

	public String itemCountLabel(String ID) {
		if(itemCountWithID(ID) <= 1) {
			return "";
		} else {
			return " (" + itemCountWithID(ID) + ")";
		}
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
	
	public List<Item> getUniqueItems() {
		List<Item> uniqueItems = new ArrayList<>();
		for(List<Item> current : items.values()) {
			uniqueItems.add(current.get(0));
		}
		return uniqueItems;
	}

	public List<Action> getStoreActions(Noun owner, Inventory other) {
		List<Action> actions = new ArrayList<>();
		for(List<Item> current : items.values()) {
			actions.add(new ActionInventoryStore(owner, other, current.get(0)));
			if(current.size() > 1) {
				actions.add(new ActionInventoryStoreAll(owner, other, current));
			}
		}
		return actions;
	}

	public List<Action> getExternalActions(Noun owner, Actor subject) {
		List<Action> actions = new ArrayList<>();
		for(List<Item> current : items.values()) {
			actions.add(new ActionInventoryTake(owner, this, current.get(0)));
			if(current.size() > 1) {
				actions.add(new ActionInventoryTakeAll(owner, this, current));
			}
		}
		actions.addAll(subject.inventory().getStoreActions(owner, this));
		return actions;
	}
	
}
