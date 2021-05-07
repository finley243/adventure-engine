package com.github.finley243.adventureengine.world.template;

public class StatsConsumable extends StatsItem {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private String name;
	private ConsumableType type;
	
	public StatsConsumable() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public ConsumableType getType() {
		return type;
	}
	
}
