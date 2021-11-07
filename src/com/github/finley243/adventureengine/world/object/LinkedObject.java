package com.github.finley243.adventureengine.world.object;

/**
 * An object that is linked to another object (both stored with an ID so they can reference each other)
 */
public abstract class LinkedObject extends WorldObject {

	private final String ID;
	
	public LinkedObject(String ID, String name, String description) {
		super(name, description);
		this.ID = ID;
	}
	
	public String getID() {
		return ID;
	}

	@Override
	public int hashCode() {
		return ID.hashCode();
	}

}
