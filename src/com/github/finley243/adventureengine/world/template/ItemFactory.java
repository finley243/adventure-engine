package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemConsumable;

public class ItemFactory {

	public static Item create(StatsItem stats) {
		if(stats instanceof StatsConsumable) {
			return new ItemConsumable();
		}
		return null;
	}
	
}
