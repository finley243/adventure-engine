package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsKey;

public class ItemKey extends Item {

	private StatsKey stats;
	
	public ItemKey(StatsKey stats) {
		super(stats.getName());
		this.stats = stats;
	}
	
	@Override
	public String getID() {
		return stats.getID();
	}

}
