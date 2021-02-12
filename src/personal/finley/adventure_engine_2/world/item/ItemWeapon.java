package personal.finley.adventure_engine_2.world.item;

import personal.finley.adventure_engine_2.world.template.TemplateWeapon;

public class ItemWeapon extends Item {
	
	private TemplateWeapon template;
	
	public ItemWeapon(String ID, String areaID, TemplateWeapon template) {
		super(ID, areaID, template.getName());
		this.template = template;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemWeapon)) {
			return false;
		} else {
			ItemWeapon other = (ItemWeapon) o;
			return this.template == other.template;
		}
	}

}
