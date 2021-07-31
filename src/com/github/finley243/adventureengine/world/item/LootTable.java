package com.github.finley243.adventureengine.world.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LootTable {

	private boolean useAll;
	private List<LootTableEntry> entries;
	
	public LootTable(boolean useAll, List<LootTableEntry> entries) {
		this.useAll = useAll;
		this.entries = entries;
	}
	
	public Set<Item> generateItems() {
		Set<Item> generatedItems = new HashSet<Item>();
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
