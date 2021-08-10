package com.github.finley243.adventureengine.world.template;

public class StatsApparel extends StatsItem {

	public enum ApparelType{
		BODY, FACE, HEAD, FEET, ARMS, LEGS
	}
	
	private ApparelType type;
	
	public StatsApparel(String ID, String name, String description, int price) {
		super(ID, name, description, price);
	}
	
	public ApparelType getType() {
		return type;
	}
	
}
