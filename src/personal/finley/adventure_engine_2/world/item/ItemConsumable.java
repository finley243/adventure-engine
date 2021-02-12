package personal.finley.adventure_engine_2.world.item;

import personal.finley.adventure_engine_2.world.template.TemplateConsumable;

public class ItemConsumable extends Item {

	private TemplateConsumable template;
	
	public ItemConsumable(String ID, String areaID, TemplateConsumable template) {
		super(ID, areaID, template.getName());
		this.template = template;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemConsumable)) {
			return false;
		} else {
			ItemConsumable other = (ItemConsumable) o;
			return this.template == other.template;
		}
	}
	
}
