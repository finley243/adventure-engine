package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemApparel;
import com.github.finley243.adventureengine.world.item.ItemEquippable;
import com.github.finley243.adventureengine.world.item.ItemFactory;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;

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
	private final Map<String, Item> itemsStateless;
	
	public Inventory(Game game, Actor actor) {
		this.game = game;
		this.actor = actor;
		this.items = new HashMap<>();
		this.itemsStateless = new HashMap<>();
	}

	public List<Item> getAllItems() {
		List<Item> allItems = new ArrayList<>();
		for(List<Item> itemList : items.values()) {
			allItems.addAll(itemList);
		}
		allItems.addAll(itemsStateless.values());
		return allItems;
	}
	
	public void addItem(Item item) {
		if(item.getTemplate().hasState()) {
			if(!items.containsKey(item.getTemplate().getID())) {
				items.put(item.getTemplate().getID(), new ArrayList<>());
			}
			items.get(item.getTemplate().getID()).add(item);
		} else {
			if(!itemsStateless.containsKey(item.getTemplate().getID())) {
				itemsStateless.put(item.getTemplate().getID(), item);
			} else {
				int addCount = item.getCount();
				itemsStateless.get(item.getTemplate().getID()).addCount(addCount);
			}
		}
	}
	
	public void addItems(List<Item> itemList) {
		for(Item item : itemList) {
			addItem(item);
		}
	}

	public boolean hasItem(String itemID) {
		return hasItem(game.data().getItem(itemID));
	}
	
	public boolean hasItem(ItemTemplate item) {
		if (item.hasState()) {
			return items.containsKey(item.getID());
		} else {
			return itemsStateless.containsKey(item.getID());
		}
	}

	public boolean hasItemWithTag(String tag) {
		for (Item item : getUniqueItems()) {
			if (item.getTemplate().getTags().contains(tag)) {
				return true;
			}
		}
		return false;
	}

	public int itemCount(String itemID) {
		return itemCount(game.data().getItem(itemID));
	}

	public int itemCount(ItemTemplate item) {
		if(item.hasState()) {
			if(!items.containsKey(item.getID())) return 0;
			return items.get(item.getID()).size();
		} else {
			if(!itemsStateless.containsKey(item.getID())) return 0;
			return itemsStateless.get(item.getID()).getCount();
		}
	}

	public String itemCountLabel(ItemTemplate item) {
		if(itemCount(item) <= 1) {
			return "";
		} else {
			return " (" + itemCount(item) + ")";
		}
	}

	public Item removeItem(ItemTemplate item) {
		if (item.hasState()) {
			if (items.containsKey(item.getID())) {
				List<Item> itemList = items.get(item.getID());
				Item removedItem = itemList.remove(itemList.size() - 1);
				if (actor != null) {
					if (removedItem instanceof ItemEquippable && actor.equipmentComponent().getEquippedItem() == removedItem) {
						actor.equipmentComponent().unequip((ItemEquippable) removedItem);
					}
					if (removedItem instanceof ItemApparel && actor.apparelComponent().getEquippedItems().contains(removedItem)) {
						actor.apparelComponent().unequip((ItemApparel) removedItem);
					}
				}
				if (itemList.isEmpty()) {
					items.remove(item.getID());
				}
				return removedItem;
			}
		} else {
			if (itemsStateless.containsKey(item.getID())) {
				return removeItems(item, 1).get(0);
			}
		}
		return null;
	}

	public Item removeItem(Item item) {
		if (item.getTemplate().hasState()) {
			if (items.containsKey(item.getTemplate().getID())) {
				List<Item> itemList = items.get(item.getTemplate().getID());
				itemList.remove(item);
				if (actor != null) {
					if (item instanceof ItemEquippable && actor.equipmentComponent().getEquippedItem() == item) {
						actor.equipmentComponent().unequip((ItemEquippable) item);
					}
					if (item instanceof ItemApparel && actor.apparelComponent().getEquippedItems().contains(item)) {
						actor.apparelComponent().unequip((ItemApparel) item);
					}
				}
				if (itemList.isEmpty()) {
					items.remove(item.getTemplate().getID());
				}
				return item;
			}
		} else {
			if (itemsStateless.containsKey(item.getTemplate().getID())) {
				return removeItems(item.getTemplate(), 1).get(0);
			}
		}
		return null;
	}

	public List<Item> removeItems(ItemTemplate item, int count) {
		List<Item> removedItems = new ArrayList<>();
		if (item.hasState()) {
			List<Item> matchingItems = items.get(item.getID());
			if (matchingItems.size() >= count) {
				for (int i = 0; i < count; i++) {
					Item reference = matchingItems.remove(matchingItems.size() - 1);
					removedItems.add(reference);
				}
			}
		} else {
			int removedCount = Math.max(itemsStateless.get(item.getID()).getCount(), count);
			itemsStateless.get(item.getID()).addCount(-count);
			if(itemsStateless.get(item.getID()).getCount() <= 0) {
				Item reference = itemsStateless.remove(item.getID());
				game.data().removeObject(reference.getID());
				//removedItems.add(reference);
				Item removedItem = ItemFactory.create(game, reference.getTemplate(), null);
				removedItem.addCount(removedCount - 1);
				removedItems.add(removedItem);
			}
		}
		return removedItems;
	}
	
	public void clear() {
		// TODO - Find better way to remove unreferenced items from Data (a "clean" cycle when saving or loading?)
		items.clear();
		itemsStateless.clear();
	}
	
	public List<Item> getUniqueItems() {
		List<Item> uniqueItems = new ArrayList<>();
		for(List<Item> current : items.values()) {
			uniqueItems.add(current.get(0));
		}
		uniqueItems.addAll(itemsStateless.values());
		return uniqueItems;
	}

	public List<Action> getStoreActions(Noun owner, Inventory other) {
		List<Action> actions = new ArrayList<>();
		for(List<Item> current : items.values()) {
			actions.add(new ActionInventoryStore(owner, other, current.get(0)));
			/*if(current.size() > 1) {
				actions.add(new ActionInventoryStoreAll(owner, other, current));
			}*/
		}
		// TODO - Add store actions for stacked items
		return actions;
	}

	public List<Action> getExternalActions(Noun owner, Actor subject) {
		List<Action> actions = new ArrayList<>();
		for(List<Item> current : items.values()) {
			actions.add(new ActionInventoryTake(owner, this, current.get(0)));
			/*if(current.size() > 1) {
				actions.add(new ActionInventoryTakeAll(owner, this, current.get(0).getTemplate()));
			}*/
		}
		for(Item current : itemsStateless.values()) {
			actions.add(new ActionInventoryTake(owner, this, current));
			if(current.getCount() > 1) {
				actions.add(new ActionInventoryTakeAll(owner, this, current.getTemplate()));
			}
		}
		actions.addAll(subject.inventory().getStoreActions(owner, this));
		return actions;
	}
	
}
