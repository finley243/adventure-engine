package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.actor.ApparelManager;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.Set;

public class StatsApparel extends StatsItem {
	
	private final ApparelManager.ApparelSlot slot;
	private final int damageResistance;
	private final Set<Effect> effects;
	
	public StatsApparel(String ID, String name, String description, int price, ApparelManager.ApparelSlot slot, int damageResistance, Set<Effect> effects) {
		super(ID, name, description, price);
		this.slot = slot;
		this.damageResistance = damageResistance;
		this.effects = effects;
	}
	
	public ApparelManager.ApparelSlot getSlot() {
		return slot;
	}

	public int getDamageResistance() {
		return damageResistance;
	}

	public Set<Effect> getEffects() {
		return effects;
	}
	
}
