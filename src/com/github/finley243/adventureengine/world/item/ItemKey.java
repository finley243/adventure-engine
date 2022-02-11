package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsKey;

public class ItemKey extends Item {

	private final StatsKey stats;
	
	public ItemKey(StatsKey stats) {
		super(stats.generateInstanceID(), stats.getName());
		this.stats = stats;
	}
	
	@Override
	public String getDescription() {
		return stats.getDescription();
	}
	
	@Override
	public String getStatsID() {
		return stats.getID();
	}

}
