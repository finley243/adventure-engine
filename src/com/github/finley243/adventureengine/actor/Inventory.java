package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.environment.Area;

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
	private final Map<String, StatelessItemStack> itemsStateless;

	public Inventory(Game game, Actor actor) {
		this.game = game;
		this.actor = actor;
		this.items = new HashMap<>();
		this.itemsStateless = new HashMap<>();
	}

	public void addItem(Item item) {
		if (item.hasState()) {
			if (item.getInventory() != null) throw new UnsupportedOperationException("Cannot add item " + item + " to inventory because it is still located in another inventory");
			if (!items.containsKey(item.getTemplateID())) {
				items.put(item.getTemplateID(), new ArrayList<>());
			}
			items.get(item.getTemplateID()).add(item);
			item.setInventory(this);
		} else {
			if (!itemsStateless.containsKey(item.getTemplateID())) {
				Item instanceToAdd = item;
				if (item.getInventory() != null) {
					instanceToAdd = ItemFactory.create(game, item.getTemplateID());
				}
				itemsStateless.put(item.getTemplateID(), new StatelessItemStack(instanceToAdd, 1));
				instanceToAdd.setInventory(this);
			} else {
				itemsStateless.get(item.getTemplateID()).count += 1;
			}
		}
	}

	public void addItems(String itemID, int count) {
		if (count <= 0) throw new IllegalArgumentException("Cannot add non-positive number of Items: " + itemID);
		for (int i = 0; i < count; i++) {
			Item instance = ItemFactory.create(game, itemID);
			addItem(instance);
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
		int totalCount = 0;
		if (itemsStateless.containsKey(itemID)) {
			totalCount += itemsStateless.get(itemID).count;
		}
		if (items.containsKey(itemID)) {
			totalCount += items.get(itemID).size();
		}
		return totalCount >= count;
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
		if (item.hasState() && items.containsKey(item.getTemplateID()) && items.get(item.getTemplateID()).contains(item)) {
			return 1;
		} else if (!item.hasState() && itemsStateless.containsKey(item.getTemplateID())) {
			return itemsStateless.get(item.getTemplateID()).count;
		}
		return 0;
	}

	public int itemCount(String itemID) {
		int totalCount = 0;
		if (items.containsKey(itemID)) {
			totalCount += items.get(itemID).size();
		}
		if (itemsStateless.containsKey(itemID)) {
			totalCount += itemsStateless.get(itemID).count;
		}
		return totalCount;
	}

	public void removeItem(Item item) {
		if (item.hasState()) {
			if (items.containsKey(item.getTemplateID())) {
				boolean wasRemoved = items.get(item.getTemplateID()).remove(item);
				if (items.get(item.getTemplateID()).isEmpty()) {
					items.remove(item.getTemplateID());
				}
				if (wasRemoved) {
					item.setInventory(null);
				}
				if (wasRemoved && actor != null) {
					if (item instanceof ItemEquippable) {
						actor.getEquipmentComponent().unequip((ItemEquippable) item);
					}
				}
			}
		} else {
			if (itemsStateless.containsKey(item.getTemplateID())) {
				int count = itemsStateless.get(item.getTemplateID()).count;
				int newCount = count - 1;
				if (newCount <= 0) {
					itemsStateless.get(item.getTemplateID()).instance.setInventory(null);
					itemsStateless.remove(item.getTemplateID());
					if (actor != null) {
						if (item instanceof ItemEquippable) {
							actor.getEquipmentComponent().unequip((ItemEquippable) item);
						}
					}
				} else {
					itemsStateless.get(item.getTemplateID()).count = newCount;
				}
			}
		}
	}

	public void removeItems(String itemID, int count) {
		if (count <= 0) throw new IllegalArgumentException("Cannot remove non-positive number of items: " + itemID);
		int countRemaining = count;
		if (itemsStateless.containsKey(itemID)) {
			int currentCount = itemsStateless.get(itemID).count;
			int newCount = currentCount - count;
			countRemaining -= Math.max(0, currentCount - countRemaining);
			if (newCount <= 0) {
				itemsStateless.get(itemID).instance.setInventory(null);
				itemsStateless.remove(itemID);
			} else {
				itemsStateless.get(itemID).count = newCount;
			}
		}
		if (countRemaining > 0 && items.containsKey(itemID)) {
			for (int i = 0; i < countRemaining; i++) {
				if (!items.get(itemID).isEmpty()) {
					int lastIndex = items.get(itemID).size() - 1;
					items.get(itemID).get(lastIndex).setInventory(null);
					items.get(itemID).remove(lastIndex);
				}
			}
		}
	}
	
	public void clear() {
		for (Item item : getItems()) {
			item.setInventory(null);
		}
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
			itemMap.put(itemsStateless.get(itemID).instance, itemsStateless.get(itemID).count);
		}
		return itemMap;
	}

	public List<Item> getItems() {
		List<Item> uniqueItems = new ArrayList<>();
		for (List<Item> current : items.values()) {
			uniqueItems.addAll(current);
		}
		for (String current : itemsStateless.keySet()) {
			Item item = itemsStateless.get(current).instance;
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
		for (StatelessItemStack stack : itemsStateless.values()) {
			//state.add(new SaveData(null, null, "itemStateless", itemType, itemsStateless.get(itemType)));
			state.add(new SaveData(null, null, "item", stack.instance.getID(), stack.count));
		}
		return state;
	}

	private static class StatelessItemStack {
		public Item instance;
		public int count;

		public StatelessItemStack(Item instance, int count) {
			this.instance = instance;
			this.count = count;
		}
	}

}
