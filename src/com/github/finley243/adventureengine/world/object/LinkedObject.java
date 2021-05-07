package com.github.finley243.adventureengine.world.object;

public abstract class LinkedObject extends WorldObject {

	private String ID;
	
	public LinkedObject(String ID, String areaID, String name) {
		super(areaID, name);
		this.ID = ID;
	}
	
	public String getID() {
		return ID;
	}

}
