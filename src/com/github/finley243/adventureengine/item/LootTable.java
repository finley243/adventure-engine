package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public Map<Item, Integer> generateItems(Game game) {
		Map<Item, Integer> generatedItems = new HashMap<>();
		if (useAll) {
			for (LootTableEntry entry : entries) {
				Map<Item, Integer> entryItems = entry.generateItems(game);
				for (Item entryItem : entryItems.keySet()) {
					if (generatedItems.containsKey(entryItem)) {
						int currentCount = generatedItems.get(entryItem);
						generatedItems.put(entryItem, currentCount + entryItems.get(entryItem));
					} else {
						generatedItems.put(entryItem, entryItems.get(entryItem));
					}
				}
			}
		} else {
			LootTableEntry randomEntry = MathUtils.selectRandomFromList(entries);
			if (randomEntry != null) {
				generatedItems.putAll(randomEntry.generateItems(game));
			}
		}
		return generatedItems;
	}

}
