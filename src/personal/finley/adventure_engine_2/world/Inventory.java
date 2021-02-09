package personal.finley.adventure_engine_2.world;

import java.util.HashSet;
import java.util.Set;

import personal.finley.adventure_engine_2.world.object.item.Item;

public class Inventory {

	//private Map<Item, Integer> inventory;
	private Set<Item> inventory;
	
	public Inventory() {
		inventory = new HashSet<Item>();
	}
	
	public void addItem(Item item) {
		inventory.add(item);
	}
	
	public boolean containsItem(Item item) {
		return inventory.contains(item);
	}
	
	public void removeItem(Item item) {
		inventory.remove(item);
	}
	
	/*
	public void addItem(Item item) {
		addItems(item, 1);
	}
	
	public void addItems(Item item, int count) {
		if(inventory.containsKey(item)) {
			int currentCount = inventory.get(item);
			inventory.put(item, currentCount + count);
		} else {
			inventory.put(item, count);
		}
	}
	
	public int getItemCount(Item item) {
		return inventory.get(item);
	}
	
	public Set<Item> getItems() {
		return inventory.keySet();
	}
	
	@Override
	public String toString() {
		return inventory.toString();
	}
	*/
	
}
