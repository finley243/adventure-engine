package personal.finley.adventure_engine_2.world.object.item.template;

import personal.finley.adventure_engine_2.world.object.item.Item;

public class ItemTemplate {
	
	protected String name;
	
	public ItemTemplate(String name) {
		this.name = name;
	}
	
	public Item createItem(String areaID) {
		return new Item("TEMP_ID", areaID, name);
	}
	
}
