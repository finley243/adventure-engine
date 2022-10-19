package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApparelTemplate extends ItemTemplate {
	
	private final Set<String> slots;
	private final Map<Damage.DamageType, Integer> damageResistance;
	private final List<String> effects;
	
	public ApparelTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, String attackType, Set<String> slots, Map<Damage.DamageType, Integer> damageResistance, List<String> effects) {
		super(ID, name, description, scripts, price, attackType);
		this.slots = slots;
		this.damageResistance = damageResistance;
		this.effects = effects;
	}

	@Override
	public boolean hasState() {
		return true;
	}
	
	public Set<String> getSlots() {
		return slots;
	}

	public int getDamageResistance(Damage.DamageType type) {
		return damageResistance.getOrDefault(type, 0);
	}

	public List<String> getEffects() {
		return effects;
	}

	public void onEquip(Actor target) {
		for(String effect : getEffects()) {
			target.effectComponent().addEffect(target.game().data().getEffect(effect));
		}
	}

	public void onUnequip(Actor target) {
		for(String effect : getEffects()) {
			target.effectComponent().removeEffect(target.game().data().getEffect(effect));
		}
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("apparel");
		return tags;
	}
	
}
