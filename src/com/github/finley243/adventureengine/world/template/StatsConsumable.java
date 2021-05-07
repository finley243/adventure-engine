package com.github.finley243.adventureengine.world.template;

public class StatsConsumable extends StatsItem {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private ConsumableType type;
	
	public StatsConsumable(String ID, String name, int price) {
		super(ID, name, price);
	}
	
	public ConsumableType getType() {
		return type;
	}
	
}
