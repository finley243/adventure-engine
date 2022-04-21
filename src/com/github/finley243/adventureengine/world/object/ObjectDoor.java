package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionExitListen;
import com.github.finley243.adventureengine.action.ActionMoveExit;
import com.github.finley243.adventureengine.action.ActionExitUnlock;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectDoor extends WorldObject {

	private final String linkedDoorID;
	private boolean isLocked;
	private final Set<String> keyIDs;
	
	public ObjectDoor(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts, String linkedDoorID, Set<String> keyIDs) {
		super(game, ID, area, name, description, scripts);
		this.linkedDoorID = linkedDoorID;
		this.keyIDs = keyIDs;
		this.isLocked = !keyIDs.isEmpty();
	}
	
	public Area getLinkedArea() {
		return game().data().getObject(linkedDoorID).getArea();
	}
	
	public void unlock() {
		this.isLocked = false;
		((ObjectDoor) game().data().getObject(linkedDoorID)).isLocked = false;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public Set<String> getKeyIDs() {
		return keyIDs;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.add(new ActionExitListen(this));
		actions.add(new ActionExitUnlock(this));
		actions.add(new ActionMoveExit(this));
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
		if(!isLocked && !keyIDs.isEmpty()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "isLocked", isLocked));
		}
		return state;
	}
	
}
