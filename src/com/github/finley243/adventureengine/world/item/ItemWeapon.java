package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsWeapon;

public class ItemWeapon extends Item {
	
	private StatsWeapon stats;
	
	public ItemWeapon(String areaID, StatsWeapon stats) {
		super(areaID, stats.getName());
		this.stats = stats;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemWeapon)) {
			return false;
		} else {
			ItemWeapon other = (ItemWeapon) o;
			return this.stats == other.stats;
		}
	}

}
