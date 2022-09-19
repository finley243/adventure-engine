package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionDoorListen;
import com.github.finley243.adventureengine.action.ActionMoveDoor;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Map;

public class ObjectDoor extends WorldObject {

	private final String linkedDoorID;
	private final AreaLink.CompassDirection direction;
	private final Lock lock;

	public ObjectDoor(Game game, String ID, Area area, String name, Scene description, boolean startDisabled, boolean startHidden, Map<String, Script> scripts, List<ActionCustom> customActions, String linkedDoorID, AreaLink.CompassDirection direction, Lock lock) {
		super(game, ID, area, name, description, startDisabled, startHidden, scripts, customActions);
		this.linkedDoorID = linkedDoorID;
		this.direction = direction;
		this.lock = lock;
	}

	@Override
	public String getName() {
		String destinationName;
		if (!getLinkedArea().getRoom().equals(this.getArea().getRoom()) || this.equals(getLinkedArea().getLandmark())) {
			destinationName = getLinkedArea().getRoom().getName();
		} else {
			destinationName = getLinkedArea().getName();
		}
		return super.getName() + " to " + destinationName;
	}

	@Override
	public String getFormattedName() {
		String destinationName;
		if (!getLinkedArea().getRoom().equals(this.getArea().getRoom()) || this.equals(getLinkedArea().getLandmark())) {
			destinationName = getLinkedArea().getRoom().getFormattedName();
		} else {
			destinationName = getLinkedArea().getFormattedName();
		}
		if (!isProperName()) {
			return LangUtils.addArticle(super.getName(), !isKnown()) + " to " + destinationName;
		} else {
			return super.getName() + " to " + destinationName;
		}
	}
	
	public Area getLinkedArea() {
		return game().data().getObject(linkedDoorID).getArea();
	}

	public AreaLink.CompassDirection getDirection() {
		return direction;
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
