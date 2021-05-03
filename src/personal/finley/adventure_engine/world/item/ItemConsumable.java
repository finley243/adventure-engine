package personal.finley.adventure_engine.world.item;

import personal.finley.adventure_engine.world.template.StatsConsumable;

public class ItemConsumable extends Item {

	private StatsConsumable stats;
	
	public ItemConsumable(String ID, String areaID, StatsConsumable stats) {
		super(ID, areaID, stats.getName());
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
