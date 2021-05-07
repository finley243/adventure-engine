package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsConsumable;

public class ItemConsumable extends Item {

	private StatsConsumable stats;
	
	public ItemConsumable(String areaID, StatsConsumable stats) {
		super(areaID, stats.getName());
		this.stats = stats;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemConsumable)) {
			return false;
		} else {
			ItemConsumable other = (ItemConsumable) o;
			return this.stats == other.stats;
		}
	}
	
}
