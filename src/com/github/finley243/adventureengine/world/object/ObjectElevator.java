package com.github.finley243.adventureengine.world.object;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveElevator;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectElevator extends WorldObject {

	private final int floorNumber;
	private final String floorName;
	private final Set<String> linkedElevatorIDs;
	private boolean isLocked;
	
	public ObjectElevator(Game game, String ID, String name, String description, Map<String, Script> scripts, int floorNumber, String floorName, Set<String> linkedElevatorIDs) {
		super(game, ID, name, description, scripts);
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
	
	public Set<Area> getLinkedAreas() {
		Set<Area> areas = new HashSet<>();
		for(String elevatorID : linkedElevatorIDs) {
			areas.add(game().data().getObject(elevatorID).getArea());
		}
		return areas;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if(isLocked) {
			
		} else {
			for(String elevatorID : linkedElevatorIDs) {
				actions.add(new ActionMoveElevator(this, (ObjectElevator) game().data().getObject(elevatorID)));
			}
		}
		return actions;
	}

}
