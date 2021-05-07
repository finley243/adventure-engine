package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsApparel;

public class ItemApparel extends Item {

	private StatsApparel stats;
	
	public ItemApparel(String areaID, StatsApparel stats) {
		super(areaID, stats.getName());
		this.stats = stats;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemApparel)) {
			return false;
		} else {
			ItemApparel other = (ItemApparel) o;
			return this.stats == other.stats;
		}
	}

}
