package com.github.finley243.adventureengine.world.template;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.script.Script;

public class StatsConsumable extends StatsItem {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private final ConsumableType type;
	private final List<Effect> effects;
	
	public StatsConsumable(String ID, String name, String description, Map<String, Script> scripts, int price, ConsumableType type, List<Effect> effects) {
		super(ID, name, description, scripts, price);
		this.type = type;
		this.effects = effects;
	}
	
	public ConsumableType getType() {
		return type;
	}
	
	public List<Effect> getEffects() {
		return effects;
	}
	
}
