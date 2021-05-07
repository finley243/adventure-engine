package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveElevator;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectElevator extends LinkedObject {

	private int floorNumber;
	private String floorName;
	private Set<String> linkedElevatorIDs;
	private boolean isLocked;
	
	public ObjectElevator(String ID, String name, int floorNumber, String floorName, Set<String> linkedElevatorIDs) {
		super(ID, name);
		this.floorNumber = floorNumber;
		this.floorName = floorName;
		this.linkedElevatorIDs = linkedElevatorIDs;
		this.isLocked = false;
	}
	
	public int getFloorNumber() {
		return floorNumber;
	}
	
	public String getFloorName() {
		return floorName;
	}
	
	public void unlock() {
		this.isLocked = false;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		if(isLocked) {
			
		} else {
			for(String elevatorID : linkedElevatorIDs) {
				actions.add(new ActionMoveElevator(this, (ObjectElevator) Data.getLinkedObject(elevatorID)));
			}
		}
		return actions;
	}

}
