package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemApparel;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.*;

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

	public boolean hasItems(String itemID, int count) {
		if (itemsStateless.containsKey(itemID)) {
			return itemsStateless.get(itemID) >= count;
		} else if (items.containsKey(itemID)) {
			return items.get(itemID).size() >= count;
		} else {
			return false;
		}
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
		int itemCount = itemCount(item);
		if (itemCount > 1) {
			return " (" + itemCount + ")";
		} else if(item.getTemplate().hasState() && items.get(item.getTemplate().getID()).size() > 1) {
			int itemIndex = items.get(item.getTemplate().getID()).indexOf(item);
			return " " + (itemIndex + 1);
		} else {
			return "";
		}
	}

	public void removeItem(Item item) {
		if (item.getTemplate().hasState()) {
			if (items.containsKey(item.getTemplate().getID())) {
				boolean wasRemoved = items.get(item.getTemplate().getID()).remove(item);
				if (items.get(item.getTemplate().getID()).isEmpty()) {
					items.remove(item.getTemplate().getID());
				}
				if (wasRemoved && actor != null) {
					if (item instanceof ItemApparel) {
						actor.getApparelComponent().unequip((ItemApparel) item);
					}
					if (item instanceof ItemEquippable) {
						actor.getEquipmentComponent().unequip((ItemEquippable) item);
					}
				}
			}
		} else {
			if (itemsStateless.containsKey(item.getTemplate().getID())) {
				int count = itemsStateless.get(item.getTemplate().getID());
				int newCount = count - 1;
				if (newCount <= 0) {
					itemsStateless.remove(item.getTemplate().getID());
					if (actor != null) {
						if (item instanceof ItemApparel) {
							actor.getApparelComponent().unequip((ItemApparel) item);
						}
						if (item instanceof ItemEquippable) {
							actor.getEquipmentComponent().unequip((ItemEquippable) item);
						}
					}
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
				if (actor != null) {
					if (item instanceof ItemApparel) {
						actor.getApparelComponent().unequip((ItemApparel) item);
					}
					if (item instanceof ItemEquippable) {
						actor.getEquipmentComponent().unequip((ItemEquippable) item);
					}
				}
			} else {
				itemsStateless.put(item.getTemplate().getID(), newCount);
			}
		}
	}

	public void removeItems(String itemID, int count) {
		//if (game.data().getItem(itemID).hasState()) throw new IllegalArgumentException("Cannot remove an item with state by its ID");
		if (count <= 0) throw new IllegalArgumentException("Cannot remove non-positive number of items: " + itemID);
		if (itemsStateless.containsKey(itemID)) {
			int currentCount = itemsStateless.get(itemID);
			int newCount = currentCount - count;
			if (newCount <= 0) {
				itemsStateless.remove(itemID);
			} else {
				itemsStateless.put(itemID, newCount);
			}
		} else if (items.containsKey(itemID)) {
			for (int i = 0; i < count; i++) {
				if (!items.get(itemID).isEmpty()) {
					items.get(itemID).remove(items.get(itemID).size() - 1);
				}
			}
		}
	}
	
	public void clear() {
		// TODO - Find way to remove unreferenced items from Data (a "clean" cycle when saving or loading?)
		items.clear();
		itemsStateless.clear();
	}

	public Map<Item, Integer> getItemMap() {
		Map<Item, Integer> itemMap = new HashMap<>();
		for (List<Item> itemList : items.values()) {
			for (Item item : itemList) {
				itemMap.put(item, 1);
			}
		}
		for (String itemID : itemsStateless.keySet()) {
			itemMap.put(ItemFactory.create(game, itemID), itemsStateless.get(itemID));
		}
		return itemMap;
	}

	public List<Item> getItems() {
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

	public List<Action> getAreaActions(Area area) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionItemTake(area, item));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionItemTake(area, item));
			if (itemCount(current) > 1) {
				actions.add(new ActionItemTakeAll(area, item));
			}
		}
		return actions;
	}

	public List<Action> getExternalActions(Noun owner, String name, Actor subject, boolean isExposed) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionInventoryTake(owner, name, this, item));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionInventoryTake(owner, name, this, item));
			if (itemCount(current) > 1) {
				actions.add(new ActionInventoryTakeAll(owner, name, this, item));
			}
		}
		if (subject != null) {
			actions.addAll(subject.getInventory().getStoreActions(owner, name, this, isExposed));
		}
		return actions;
	}

	private List<Action> getStoreActions(Noun owner, String name, Inventory other, boolean isExposed) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionInventoryStore(owner, name, other, item, isExposed));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionInventoryStore(owner, name, other, item, isExposed));
			if (itemCount(current) > 1) {
				actions.add(new ActionInventoryStoreAll(owner, name, other, item, isExposed));
			}
		}
		return actions;
	}

	public void loadState(SaveData data) {
		if (data.getParameter().equals("inventory")) {
			for (SaveData subData : data.getValueMulti()) {
				switch (subData.getParameter()) {
					case "item":
						addItem(game.data().getItemState(subData.getValueString()));
						break;
					case "itemStateless":
						addItems(ItemFactory.create(game, subData.getValueString()), subData.getValueInt());
						break;
				}
			}
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		for (String itemType : items.keySet()) {
			for (Item item : items.get(itemType)) {
				state.add(new SaveData(null, null, "item", item.getID()));
			}
		}
		for (String itemType : itemsStateless.keySet()) {
			state.add(new SaveData(null, null, "itemStateless", itemType, itemsStateless.get(itemType)));
		}
		return state;
	}

}
