package com.github.finley243.adventureengine.world.template;

public class StatsConsumable extends StatsItem {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private ConsumableType type;
	
	public StatsConsumable(String ID, String name) {
		super(ID, name);
	}
	
	public ConsumableType getType() {
		return type;
	}
	
}
