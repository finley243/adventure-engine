package com.github.finley243.adventureengine.world.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LootTable {

	private final String ID;
	private final boolean useAll;
	private final List<LootTableEntry> entries;
	
	public LootTable(String ID, boolean useAll, List<LootTableEntry> entries) {
		this.ID = ID;
		this.useAll = useAll;
		this.entries = entries;
	}
	
	public String getID() {
		return ID;
	}
	
	public Set<Item> generateItems() {
		Set<Item> generatedItems = new HashSet<>();
		if(useAll) {
			for(LootTableEntry entry : entries) {
				generatedItems.addAll(entry.generateItems());
			}
		} else {
			generatedItems.addAll(getRandomEntry().generateItems());
		}
		return generatedItems;
	}
	
	private LootTableEntry getRandomEntry() {
		return entries.get(ThreadLocalRandom.current().nextInt(entries.size()));
	}

}
