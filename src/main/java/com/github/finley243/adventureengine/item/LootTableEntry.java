package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.item.component.ItemComponentMod;
import com.github.finley243.adventureengine.item.component.ItemComponentModdable;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LootTableEntry {

	private final LootTable tableReference;
	private final ItemTemplate itemReference;
	private final float chance;
	private final int countMin;
	private final int countMax;
	private final LootTable modTable;
	private final float modChance;
	
	public LootTableEntry(LootTable tableReference, float chance, int countMin, int countMax, LootTable modTable, float modChance) {
		if (tableReference == null) throw new IllegalArgumentException("LootTableEntry reference cannot be null");
		this.tableReference = tableReference;
		this.itemReference = null;
		this.chance = chance;
		this.countMin = countMin;
		this.countMax = countMax;
		this.modTable = modTable;
		this.modChance = modChance;
	}

	public LootTableEntry(ItemTemplate itemReference, float chance, int countMin, int countMax, LootTable modTable, float modChance) {
		if (itemReference == null) throw new IllegalArgumentException("LootTableEntry reference cannot be null");
		this.tableReference = null;
		this.itemReference = itemReference;
		this.chance = chance;
		this.countMin = countMin;
		this.countMax = countMax;
		this.modTable = modTable;
		this.modChance = modChance;
	}

	public void generateItems(Inventory inventory, ItemFactory itemFactory, MutableRegistry<Item> itemMutableRegistry) {
		int count = ThreadLocalRandom.current().nextInt(countMin, countMax + 1);
		if (MathUtils.randomCheck(chance)) {
			if (tableReference != null) {
				for (int i = 0; i < count; i++) {
					tableReference.generateItems(inventory, itemFactory, itemMutableRegistry);
				}
			} else if (itemReference != null) {
				for (int i = 0; i < count; i++) {
					Item itemInstance = itemFactory.createWithGenID(itemReference);
					if (modTable != null && itemInstance.hasComponentOfType(ItemComponentModdable.class) && MathUtils.randomCheck(modChance)) {
						Inventory modInventory = new Inventory(null);
						modTable.generateItems(inventory, itemFactory, itemMutableRegistry);
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
					}
					inventory.addItem(itemInstance, itemMutableRegistry);
				}
			} else {
				throw new IllegalStateException("LootTableEntry has no valid LootTable or ItemTemplate reference (should be impossible)");
			}
		}
	}
	
}
