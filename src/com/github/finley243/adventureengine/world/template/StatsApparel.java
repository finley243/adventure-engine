package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.actor.EquipmentComponent;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class StatsApparel extends StatsItem {
	
	private final EquipmentComponent.ApparelSlot slot;
	private final int damageResistance;
	private final List<Effect> effects;
	
	public StatsApparel(String ID, String name, String description, int price, EquipmentComponent.ApparelSlot slot, int damageResistance, List<Effect> effects) {
		super(ID, name, description, price);
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
