package personal.finley.adventure_engine_2.actor;

import java.util.HashSet;
import java.util.Set;

import personal.finley.adventure_engine_2.world.item.Item;

public class Inventory {

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
	
}
