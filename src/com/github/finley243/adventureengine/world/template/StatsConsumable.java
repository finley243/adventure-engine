package com.github.finley243.adventureengine.world.template;

import java.util.List;

import com.github.finley243.adventureengine.effect.Effect;

public class StatsConsumable extends StatsItem {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private ConsumableType type;
	private List<Effect> effects;
	
	public StatsConsumable(String ID, String name, String description, int price, ConsumableType type, List<Effect> effects) {
		super(ID, name, description, price);
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
