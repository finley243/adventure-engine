package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;

import java.util.ArrayList;
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
	
	public List<Item> generateItems(Game game) {
		List<Item> generatedItems = new ArrayList<>();
		if(useAll) {
			for(LootTableEntry entry : entries) {
				generatedItems.addAll(entry.generateItems(game));
			}
		} else {
			generatedItems.addAll(getRandomEntry().generateItems(game));
		}
		return generatedItems;
	}
	
	private LootTableEntry getRandomEntry() {
		return entries.get(ThreadLocalRandom.current().nextInt(entries.size()));
	}

}
