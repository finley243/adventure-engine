package personal.finley.adventure_engine_2.world.object.item;

import personal.finley.adventure_engine_2.EnumTypes.ApparelType;

public class ItemApparel extends Item {

	private ApparelType apparelType;
	
	public ItemApparel(String ID, String name, ApparelType type) {
		super(ID, name);
		this.apparelType = type;
	}

}
