package personal.finley.adventure_engine_2.world.object.item;

import personal.finley.adventure_engine_2.EnumTypes.ApparelType;

public class ItemApparel extends Item {

	private ApparelType apparelType;
	
	public ItemApparel(String ID, String currentAreaID, String name, ApparelType type) {
		super(ID, currentAreaID, name);
		this.apparelType = type;
	}

}
