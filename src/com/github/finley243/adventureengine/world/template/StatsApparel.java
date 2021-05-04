package com.github.finley243.adventureengine.world.template;

public class StatsApparel {

	public enum ApparelType{
		BODY, FACE, HEAD, FEET, ARMS, LEGS
	}
	
	private String name;
	private ApparelType type;
	
	public StatsApparel() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public ApparelType getType() {
		return type;
	}
	
}