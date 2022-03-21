package com.github.finley243.adventureengine.world.item.stats;

import com.github.finley243.adventureengine.actor.component.EquipmentComponent;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;
import java.util.Map;

public class StatsApparel extends StatsItem {
	
	private final EquipmentComponent.ApparelSlot slot;
	private final int damageResistance;
	private final List<Effect> effects;
	
	public StatsApparel(String ID, String name, String description, Map<String, Script> scripts, int price, EquipmentComponent.ApparelSlot slot, int damageResistance, List<Effect> effects) {
		super(ID, name, description, scripts, price);
		this.slot = slot;
		this.damageResistance = damageResistance;
		this.effects = effects;
	}
	
	public EquipmentComponent.ApparelSlot getSlot() {
		return slot;
	}

	public int getDamageResistance() {
		return damageResistance;
	}

	public List<Effect> getEffects() {
		return effects;
	}
	
}
