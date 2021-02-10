package personal.finley.adventure_engine_2.world.object.item;

import personal.finley.adventure_engine_2.EnumTypes.ConsumableType;

public class ItemConsumable extends Item {

	private ConsumableType consumableType;
	
	public ItemConsumable(String ID, String currentAreaID, String name, ConsumableType type) {
		super(ID, currentAreaID, name);
		this.consumableType = type;
	}
	
}
