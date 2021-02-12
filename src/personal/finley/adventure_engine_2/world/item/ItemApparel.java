package personal.finley.adventure_engine_2.world.item;

import personal.finley.adventure_engine_2.world.template.TemplateApparel;

public class ItemApparel extends Item {

	private TemplateApparel template;
	
	public ItemApparel(String ID, String areaID, TemplateApparel template) {
		super(ID, areaID, template.getName());
		this.template = template;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemApparel)) {
			return false;
		} else {
			ItemApparel other = (ItemApparel) o;
			return this.template == other.template;
		}
	}

}
