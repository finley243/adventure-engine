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
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectElevator extends WorldObject {

	private final int floorNumber;
	private final String floorName;
	private final Set<String> linkedElevatorIDs;
	private final boolean startLocked;
	private boolean isLocked;
	
	public ObjectElevator(Game game, String ID, String name, String description, Map<String, Script> scripts, int floorNumber, String floorName, Set<String> linkedElevatorIDs, boolean startLocked) {
		super(game, ID, name, description, scripts);
		this.floorNumber = floorNumber;
		this.floorName = floorName;
		this.linkedElevatorIDs = linkedElevatorIDs;
		this.startLocked = startLocked;
		this.isLocked = startLocked;
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
			// Hack?
		} else {
			for(String elevatorID : linkedElevatorIDs) {
				actions.add(new ActionMoveElevator(this, (ObjectElevator) game().data().getObject(elevatorID)));
			}
		}
		return actions;
	}

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "isLocked":
				isLocked = saveData.getValueBoolean();
				break;
			default:
				super.loadState(saveData);
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if(isLocked != startLocked) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "isLocked", isLocked));
		}
		return state;
	}

}
