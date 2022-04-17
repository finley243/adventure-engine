package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {

	private final Game game;
	// If inventory belongs to an object or secondary component (e.g. vendor inventory), actor will be null
	private final Actor actor;
	// Keys are statsIDs, values are lists of items with the corresponding statsID
	private final Map<String, List<Item>> items;
	private final Map<String, Integer> itemsStateless;

	public Inventory(Game game, Actor actor) {
		this.game = game;
		this.actor = actor;
		this.items = new HashMap<>();
		this.itemsStateless = new HashMap<>();
	}

	/*public List<Item> getAllItems() {
		List<Item> allItems = new ArrayList<>();
		for(List<Item> itemList : items.values()) {
			allItems.addAll(itemList);
		}
		allItems.addAll(itemsStateless.values());
		return allItems;
	}*/

	public void addItem(Item item) {
		if (item.getTemplate().hasState()) {
			if (!items.containsKey(item.getTemplate().getID())) {
				items.put(item.getTemplate().getID(), new ArrayList<>());
			}
			items.get(item.getTemplate().getID()).add(item);
		} else {
			int currentCount = itemsStateless.getOrDefault(item.getTemplate().getID(), 0);
			itemsStateless.put(item.getTemplate().getID(), currentCount + 1);
		}
	}

	public void addItems(Item item, int count) {
		if (item.getTemplate().hasState()) throw new IllegalArgumentException("Cannot add multiple Items with state: " + item.getTemplate().getID());
		if (count <= 0) throw new IllegalArgumentException("Cannot add non-positive number of Items: " + item.getTemplate().getID());
		int currentCount = itemsStateless.getOrDefault(item.getTemplate().getID(), 0);
		itemsStateless.put(item.getTemplate().getID(), currentCount + count);
	}

	public void addItems(Map<Item, Integer> itemMap) {
		for (Item item : itemMap.keySet()) {
			if (item.getTemplate().hasState()) {
				addItem(item);
			} else {
				addItems(item, itemMap.get(item));
			}
		}
	}

	public boolean hasItem(String itemID) {
		return itemsStateless.containsKey(itemID) || items.containsKey(itemID);
	}

	public boolean hasItemWithTag(String tag) {
		for (String current : items.keySet()) {
			if (game.data().getItem(current).getTags().contains(tag)) {
				return true;
			}
		}
		for (String current : itemsStateless.keySet()) {
			if (game.data().getItem(current).getTags().contains(tag)) {
				return true;
			}
		}
		return false;
	}

	public int itemCount(Item item) {
		if (item.getTemplate().hasState()) {
			if (items.containsKey(item.getTemplate().getID()) && items.get(item.getTemplate().getID()).contains(item)) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return itemsStateless.getOrDefault(item.getTemplate().getID(), 0);
		}
	}

	public int itemCount(String itemID) {
		if (game.data().getItem(itemID).hasState()) {
			if (items.containsKey(itemID)) {
				return items.get(itemID).size();
			} else {
				return 0;
			}
		} else {
			return itemsStateless.getOrDefault(itemID, 0);
		}
	}

	public String itemCountLabel(Item item) {
		if (itemCount(item) > 1) {
			return " (" + itemCount(item) + ")";
		} else {
			return "";
		}
	}

	public String itemCountLabel(String itemID) {
		if (itemCount(itemID) > 1) {
			return " (" + itemCount(itemID) + ")";
		} else {
			return "";
		}
	}

	public void removeItem(Item item) {
		if (item.getTemplate().hasState()) {
			if (items.containsKey(item.getTemplate().getID())) {
				items.get(item.getTemplate().getID()).remove(item);
				if (items.get(item.getTemplate().getID()).isEmpty()) {
					items.remove(item.getTemplate().getID());
				}
			}
		} else {
			if (itemsStateless.containsKey(item.getTemplate().getID())) {
				int count = itemsStateless.get(item.getTemplate().getID());
				int newCount = count - 1;
				if (newCount <= 0) {
					itemsStateless.remove(item.getTemplate().getID());
				} else {
					itemsStateless.put(item.getTemplate().getID(), newCount);
				}
			}
		}
	}

	public void removeItems(Item item, int count) {
		if (item.getTemplate().hasState()) throw new IllegalArgumentException("Cannot remove multiple items with state: " + item.getTemplate().getID());
		if (count <= 0) throw new IllegalArgumentException("Cannot remove non-positive number of items: " + item.getTemplate().getID());
		if (itemsStateless.containsKey(item.getTemplate().getID())) {
			int currentCount = itemsStateless.get(item.getTemplate().getID());
			int newCount = currentCount - count;
			if (newCount <= 0) {
				itemsStateless.remove(item.getTemplate().getID());
			} else {
				itemsStateless.put(item.getTemplate().getID(), newCount);
			}
		}
	}

	public void removeItems(String itemID, int count) {
		if (game.data().getItem(itemID).hasState()) throw new IllegalArgumentException("Cannot remove an item with state by its ID");
		if (count <= 0) throw new IllegalArgumentException("Cannot remove non-positive number of items: " + itemID);
		if (itemsStateless.containsKey(itemID)) {
			int currentCount = itemsStateless.get(itemID);
			int newCount = currentCount - count;
			if (newCount <= 0) {
				itemsStateless.remove(itemID);
			} else {
				itemsStateless.put(itemID, newCount);
			}
		}
	}
	
	public void clear() {
		// TODO - Find way to remove unreferenced items from Data (a "clean" cycle when saving or loading?)
		items.clear();
		itemsStateless.clear();
		//itemStacks.clear();
	}

	public List<Item> getUniqueItems() {
		List<Item> uniqueItems = new ArrayList<>();
		for (List<Item> current : items.values()) {
			uniqueItems.addAll(current);
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			uniqueItems.add(item);
		}
		return uniqueItems;
	}

	public List<Action> getExternalActions(Noun owner, Actor subject) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionInventoryTake(owner, this, item));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionInventoryTake(owner, this, item));
			if (itemCount(current) > 1) {
				actions.add(new ActionInventoryTakeAll(owner, this, item));
			}
		}
		actions.addAll(subject.inventory().getStoreActions(owner, this));
		return actions;
	}

	private List<Action> getStoreActions(Noun owner, Inventory other) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionInventoryStore(owner, other, item));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionInventoryStore(owner, other, item));
			if (itemCount(current) > 1) {
				actions.add(new ActionInventoryStoreAll(owner, other, item));
			}
		}
		return actions;
	}
	
}
