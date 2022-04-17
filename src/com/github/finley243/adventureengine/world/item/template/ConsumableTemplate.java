package com.github.finley243.adventureengine.world.item.template;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.script.Script;

public class ConsumableTemplate extends ItemTemplate {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private final ConsumableType type;
	private final List<Effect> effects;
	
	public ConsumableTemplate(String ID, String name, String description, Map<String, Script> scripts, int price, ConsumableType type, List<Effect> effects) {
		super(ID, name, description, scripts, price);
		this.type = type;
		this.effects = effects;
	}

	@Override
	public boolean hasState() {
		return false;
	}
	
	public ConsumableType getType() {
		return type;
	}
	
	public List<Effect> getEffects() {
		return effects;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("consumable");
		return tags;
	}
	
}
