package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.component.ItemComponentMod;
import com.github.finley243.adventureengine.item.component.ItemComponentModdable;

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

	public void generateItems(Game game, Inventory inventory) {
		int count = ThreadLocalRandom.current().nextInt(countMin, countMax + 1);
		if (MathUtils.randomCheck(chance)) {
			if (isLootTable) {
				LootTable table = game.data().getLootTable(referenceID);
				for (int i = 0; i < count; i++) {
					table.generateItems(game, inventory);
				}
			} else {
				for (int i = 0; i < count; i++) {
					Item itemInstance = ItemFactory.create(game, referenceID);
					if (modReference != null && itemInstance.hasComponentOfType(ItemComponentModdable.class) && MathUtils.randomCheck(modChance)) {
						if (modIsTable) {
							Inventory modInventory = new Inventory(game, null);
							game.data().getLootTable(modReference).generateItems(game, inventory);
							for (Map.Entry<Item, Integer> entry : modInventory.getItemMap().entrySet()) {
								Item modItem = entry.getKey();
								int modCount = entry.getValue();
								if (modItem.hasComponentOfType(ItemComponentMod.class)) {
									for (int j = 0; j < modCount; j++) {
										if (itemInstance.getComponentOfType(ItemComponentModdable.class).canInstallMod(modItem)) {
											itemInstance.getComponentOfType(ItemComponentModdable.class).installMod(modItem);
										}
									}
								}
							}
							modInventory.clear();
						} else {
							Item modItem = ItemFactory.create(game, modReference);
							if (modItem.hasComponentOfType(ItemComponentMod.class)) {
								itemInstance.getComponentOfType(ItemComponentModdable.class).installMod(modItem);
							}
						}
					}
					inventory.addItem(itemInstance);
				}
			}
		}
	}
	
}
