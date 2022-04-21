package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LootTableEntry {

	private final String referenceID;
	private final boolean isLootTable;
	private final float chance;
	private final int count;
	
	public LootTableEntry(String referenceID, boolean isLootTable, float chance, int count) {
		this.referenceID = referenceID;
		this.isLootTable = isLootTable;
		this.chance = chance;
		this.count = count;
	}

	public Map<Item, Integer> generateItems(Game game) {
		Map<Item, Integer> items = new HashMap<>();
		if (ThreadLocalRandom.current().nextFloat() < chance) {
			if (isLootTable) {
				LootTable table = game.data().getLootTable(referenceID);
				for (int i = 0; i < count; i++) {
					Map<Item, Integer> tableItems = table.generateItems(game);
					for (Item tableItem : tableItems.keySet()) {
						if (items.containsKey(tableItem)) {
							int currentCount = items.get(tableItem);
							items.put(tableItem, currentCount + tableItems.get(tableItem));
						} else {
							items.put(tableItem, tableItems.get(tableItem));
						}
					}
				}
			} else {
				if (game.data().getItem(referenceID).hasState()) {
					for (int i = 0; i < count; i++) {
						items.put(ItemFactory.create(game, referenceID), 1);
					}
				} else {
					items.put(ItemFactory.create(game, referenceID), count);
				}
			}
		}
		return items;
	}
	
}
