package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LootTableEntry {

	private final String referenceID;
	private final boolean isLootTable;
	private final float chance;
	private final int countMin;
	private final int countMax;
	private final String modReference;
	private final boolean modIsTable;
	private final float modChance;
	
	public LootTableEntry(String referenceID, boolean isLootTable, float chance, int countMin, int countMax, String modReference, boolean modIsTable, float modChance) {
		this.referenceID = referenceID;
		this.isLootTable = isLootTable;
		this.chance = chance;
		this.countMin = countMin;
		this.countMax = countMax;
		this.modReference = modReference;
		this.modIsTable = modIsTable;
		this.modChance = modChance;
	}

	public Map<Item, Integer> generateItems(Game game) {
		Map<Item, Integer> items = new HashMap<>();
		int count = ThreadLocalRandom.current().nextInt(countMin, countMax + 1);
		if (MathUtils.randomCheck(chance)) {
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
				if (game.data().getItemTemplate(referenceID).hasState()) {
					for (int i = 0; i < count; i++) {
						Item itemInstance = ItemFactory.create(game, referenceID);
						// TODO - Expand to other types of items besides weapons
						if (modReference != null && itemInstance instanceof ItemWeapon weapon && MathUtils.randomCheck(modChance)) {
							Map<Item, Integer> modItems;
							if (modIsTable) {
								modItems = game.data().getLootTable(modReference).generateItems(game);
							} else {
								modItems = new HashMap<>();
								modItems.put(ItemFactory.create(game, modReference), 1);
							}
							for (Map.Entry<Item, Integer> entry : modItems.entrySet()) {
								if (entry.getKey() instanceof ItemMod mod && weapon.canInstallMod(mod)) {
									for (int j = 0; j < entry.getValue(); j++) {
										weapon.installMod(mod);
									}
								}
							}
						}
						items.put(itemInstance, 1);
					}
				} else {
					items.put(ItemFactory.create(game, referenceID), count);
				}
			}
		}
		return items;
	}
	
}
