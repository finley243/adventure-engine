package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.ApparelComponent;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApparelTemplate extends ItemTemplate {
	
	private final ApparelComponent.ApparelSlot slot;
	private final Map<Damage.DamageType, Integer> damageResistance;
	private final List<Effect> effects;
	
	public ApparelTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, ApparelComponent.ApparelSlot slot, Map<Damage.DamageType, Integer> damageResistance, List<Effect> effects) {
		super(ID, name, description, scripts, price);
		this.slot = slot;
		this.damageResistance = damageResistance;
		this.effects = effects;
	}

	@Override
	public boolean hasState() {
		return true;
	}
	
	public ApparelComponent.ApparelSlot getSlot() {
		return slot;
	}

	public int getDamageResistance(Damage.DamageType type) {
		return damageResistance.getOrDefault(type, 0);
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public void onEquip(Actor target) {
		for(Effect effect : getEffects()) {
			target.effectComponent().addEffect(effect);
		}
	}

	public void onUnequip(Actor target) {
		for(Effect effect : getEffects()) {
			target.effectComponent().removeEffect(effect);
		}
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("apparel");
		return tags;
	}
	
}
