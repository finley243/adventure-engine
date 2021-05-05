package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionElevator;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectElevator extends WorldObject {

	private int floorNumber;
	private String destinationName;
	private List<String> linkedElevatorIDs;
	private boolean isLocked;
	
	public ObjectElevator(String ID, String areaID, String name, int floorNumber, String destinationName, List<String> linkedElevatorIDs) {
		super(ID, areaID, name);
		this.floorNumber = floorNumber;
		this.destinationName = destinationName;
		this.linkedElevatorIDs = linkedElevatorIDs;
	}
	
	@Override
	public String getFormattedName() {
		return super.getFormattedName();
	}
	
	public void unlock() {
		this.isLocked = false;
		//((ObjectExit) Data.getObject(linkedExitID)).isLocked = false;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		if(isLocked) {
			
		} else {
			actions.add(new ActionElevator(this));
		}
		return actions;
	}

}
