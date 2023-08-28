package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Inventory;

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

	public void generateItems(Game game, Inventory inventory) {
		if (useAll) {
			for (LootTableEntry entry : entries) {
				entry.generateItems(game, inventory);
			}
		} else {
			LootTableEntry randomEntry = MathUtils.selectRandomFromList(entries);
			if (randomEntry != null) {
				randomEntry.generateItems(game, inventory);
			}
		}
	}

}
