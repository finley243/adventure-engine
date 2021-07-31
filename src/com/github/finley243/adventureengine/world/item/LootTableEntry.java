package com.github.finley243.adventureengine.world.item;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class LootTableEntry {

	private String referenceID;
	private boolean isLootTable;
	private float chance;
	private int count;
	
	public LootTableEntry(String referenceID, boolean isLootTable, float chance, int count) {
		this.referenceID = referenceID;
		this.isLootTable = isLootTable;
		this.chance = chance;
		this.count = count;
	}
	
	public Set<Item> generateItems() {
		Set<Item> items = new HashSet<Item>();
		if(ThreadLocalRandom.current().nextFloat() < chance) {
			if(isLootTable) {
				LootTable table = Data.getLootTable(referenceID);
				for(int i = 0; i < count; i++) {
					items.addAll(table.generateItems());
				}
			} else {
				for(int i = 0; i < count; i++) {
					items.add(ItemFactory.create(referenceID));
				}
			}
		}
		return items;
	}
	
}
