package com.github.finley243.adventureengine.world.template;

public class StatsApparel extends StatsItem {

	public enum ApparelType{
		BODY, FACE, HEAD, FEET, ARMS, LEGS
	}
	
	private final ApparelType type;
	
	public StatsApparel(String ID, String name, String description, int price, ApparelType type) {
		super(ID, name, description, price);
		this.type = type;
	}
	
	public ApparelType getType() {
		return type;
	}
	
}
