package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionDoorListen;
import com.github.finley243.adventureengine.action.ActionMoveDoor;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectDoor extends WorldObject {

	private final String linkedDoorID;
	private final Lock lock;
	
	public ObjectDoor(Game game, String ID, Area area, String name, Scene description, Map<String, Script> scripts, List<ActionCustom> customActions, String linkedDoorID, Lock lock) {
		super(game, ID, area, name, description, scripts, customActions);
		this.linkedDoorID = linkedDoorID;
		this.lock = lock;
	}
	
	public Area getLinkedArea() {
		return game().data().getObject(linkedDoorID).getArea();
	}

	public ObjectDoor getLinkedDoor() {
		return (ObjectDoor) game().data().getObject(linkedDoorID);
	}

	public Lock getLock() {
		return lock;
	}

	public boolean isLocked() {
		return lock != null && lock.isLocked();
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if (!this.isGuarded()) {
			actions.add(new ActionDoorListen(this));
			actions.add(new ActionMoveDoor(this));
			if (lock != null) {
				actions.addAll(lock.getActions(subject));
			}
		}
		return actions;
	}

	public void loadState(SaveData saveData) {
		// TODO - Add lock data saving
		super.loadState(saveData);
	}

	public List<SaveData> saveState() {
		return super.saveState();
	}
	
}
