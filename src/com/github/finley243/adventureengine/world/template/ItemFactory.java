package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemApparel;
import com.github.finley243.adventureengine.world.item.ItemConsumable;
import com.github.finley243.adventureengine.world.item.ItemKey;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ItemFactory {

	public static Item create(String statsID) {
		return create(Data.getItem(statsID));
	}
	
	public static Item create(StatsItem stats) {
		if(stats instanceof StatsConsumable) {
			return new ItemConsumable((StatsConsumable) stats);
		} else if(stats instanceof StatsApparel) {
			return new ItemApparel((StatsApparel) stats);
		} else if(stats instanceof StatsWeapon) {
			return new ItemWeapon((StatsWeapon) stats);
		} else if(stats instanceof StatsKey) {
			return new ItemKey((StatsKey) stats);
		}
		return null;
	}
	
}
