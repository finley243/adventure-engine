package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.item.Item;
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
		if (item.hasState()) {
			if (!items.containsKey(item.getTemplateID())) {
				items.put(item.getTemplateID(), new ArrayList<>());
			}
			items.get(item.getTemplateID()).add(item);
		} else {
			int currentCount = itemsStateless.getOrDefault(item.getTemplateID(), 0);
			itemsStateless.put(item.getTemplateID(), currentCount + 1);
		}
	}

	public void addItems(String itemID, int count) {
		if (count <= 0) throw new IllegalArgumentException("Cannot add non-positive number of Items: " + itemID);
		if (game.data().getItemTemplate(itemID).hasState()) {
			if (!items.containsKey(itemID)) {
				items.put(itemID, new ArrayList<>());
			}
			for (int i = 0; i < count; i++) {
				Item itemInstance = ItemFactory.create(game, itemID);
				items.get(itemID).add(itemInstance);
			}
		} else {
			int currentCount = itemsStateless.getOrDefault(itemID, 0);
			itemsStateless.put(itemID, currentCount + count);
		}
	}

	public void addItems(Map<Item, Integer> itemMap) {
		for (Item item : itemMap.keySet()) {
			if (item.hasState()) {
				addItem(item);
			} else {
				addItems(item.getTemplateID(), itemMap.get(item));
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
			if (game.data().getItemTemplate(current).getTags().contains(tag)) {
				return true;
			}
		}
		for (String current : itemsStateless.keySet()) {
			if (game.data().getItemTemplate(current).getTags().contains(tag)) {
				return true;
			}
		}
		return false;
	}

	// TODO - Possibly change behavior to return count of stated items with the same template, rather than count of instance
	public int itemCount(Item item) {
		if (item.hasState()) {
			if (items.containsKey(item.getTemplateID()) && items.get(item.getTemplateID()).contains(item)) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return itemsStateless.getOrDefault(item.getTemplateID(), 0);
		}
	}

	public int itemCount(String itemID) {
		if (game.data().getItemTemplate(itemID).hasState()) {
			if (items.containsKey(itemID)) {
				return items.get(itemID).size();
			} else {
				return 0;
			}
		} else {
			return itemsStateless.getOrDefault(itemID, 0);
		}
	}

	public void removeItem(Item item) {
		if (item.hasState()) {
			if (items.containsKey(item.getTemplateID())) {
				boolean wasRemoved = items.get(item.getTemplateID()).remove(item);
				if (items.get(item.getTemplateID()).isEmpty()) {
					items.remove(item.getTemplateID());
				}
				if (wasRemoved && actor != null) {
					if (item instanceof ItemEquippable) {
						actor.getEquipmentComponent().unequip((ItemEquippable) item);
					}
				}
			}
		} else {
			if (itemsStateless.containsKey(item.getTemplateID())) {
				int count = itemsStateless.get(item.getTemplateID());
				int newCount = count - 1;
				if (newCount <= 0) {
					itemsStateless.remove(item.getTemplateID());
					if (actor != null) {
						if (item instanceof ItemEquippable) {
							actor.getEquipmentComponent().unequip((ItemEquippable) item);
						}
					}
				} else {
					itemsStateless.put(item.getTemplateID(), newCount);
				}
			}
		}
	}

	public void removeItems(String itemID, int count) {
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

	public List<Action> getExternalActions(Noun owner, String name, Actor subject, String takePrompt, String takePhrase, String storePrompt, String storePhrase, boolean enableTake, boolean enableStore) {
		List<Action> actions = new ArrayList<>();
		if (enableTake) {
			actions.addAll(getTakeActions(owner, name, takePrompt, takePhrase));
		}
		if (subject != null && enableStore) {
			actions.addAll(subject.getInventory().getStoreActions(owner, name, this, storePrompt, storePhrase));
		}
		return actions;
	}

	private List<Action> getTakeActions(Noun owner, String name, String prompt, String phrase) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionInventoryTake(owner, name, this, item, prompt, phrase));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionInventoryTake(owner, name, this, item, prompt, phrase));
			if (itemCount(current) > 1) {
				actions.add(new ActionInventoryTakeAll(owner, name, this, item, prompt, phrase));
			}
		}
		return actions;
	}

	private List<Action> getStoreActions(Noun owner, String name, Inventory other, String prompt, String phrase) {
		List<Action> actions = new ArrayList<>();
		for (List<Item> current : items.values()) {
			for (Item item : current) {
				actions.add(new ActionInventoryStore(owner, name, other, item, prompt, phrase));
			}
		}
		for (String current : itemsStateless.keySet()) {
			Item item = ItemFactory.create(game, current);
			actions.add(new ActionInventoryStore(owner, name, other, item, prompt, phrase));
			if (itemCount(current) > 1) {
				actions.add(new ActionInventoryStoreAll(owner, name, other, item, prompt, phrase));
			}
		}
		return actions;
	}

	public void loadState(SaveData data) {
		if (data.getParameter().equals("inventory")) {
			for (SaveData subData : data.getValueMulti()) {
				switch (subData.getParameter()) {
					case "item" -> addItem(game.data().getItemState(subData.getValueString()));
					case "itemStateless" ->
							addItems(subData.getValueString(), subData.getValueInt());
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

	public static String getItemNameFormatted(Item item, Inventory inventory) {
		if (item.hasState() && inventory.itemCount(item.getTemplateID()) > 1) {
			int instanceIndex = inventory.items.get(item.getTemplateID()).indexOf(item) + 1;
			return LangUtils.titleCase(item.getName()) + " (" + instanceIndex + ")";
		} else if (inventory.itemCount(item) > 1) {
			return LangUtils.titleCase(item.getName()) + " x" + inventory.itemCount(item);
		} else {
			return LangUtils.titleCase(item.getName());
		}
	}

}
