package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsKey;

public class ItemKey extends Item {

	private final StatsKey stats;
	
	public ItemKey(StatsKey stats) {
		super(stats.getName());
		this.stats = stats;
	}
	
	@Override
	public String getDescription() {
		return stats.getDescription();
	}
	
	@Override
	public String getID() {
		return stats.getID();
	}
	
	@Override
	public boolean equalsInventory(Item other) {
		if(!(other instanceof ItemKey)) {
			return false;
		} else {
			return ((ItemKey) other).stats.equals(this.stats);
		}
	}

}
