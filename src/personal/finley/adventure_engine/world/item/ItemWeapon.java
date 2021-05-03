package personal.finley.adventure_engine.world.item;

import personal.finley.adventure_engine.world.template.StatsWeapon;

public class ItemWeapon extends Item {
	
	private StatsWeapon stats;
	
	public ItemWeapon(String ID, String areaID, StatsWeapon stats) {
		super(ID, areaID, stats.getName());
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
