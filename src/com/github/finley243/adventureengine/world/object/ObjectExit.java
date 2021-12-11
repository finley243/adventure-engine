package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionExitListen;
import com.github.finley243.adventureengine.action.ActionMoveExit;
import com.github.finley243.adventureengine.action.ActionExitUnlock;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectExit extends WorldObject {

	private final String linkedExitID;
	private boolean isLocked;
	private final Set<String> keyIDs;
	
	public ObjectExit(String ID, String name, String description, String linkedExitID, Set<String> keyIDs) {
		super(ID, name, description);
		this.linkedExitID = linkedExitID;
		this.keyIDs = keyIDs;
		this.isLocked = !keyIDs.isEmpty();
	}
	
	public Area getLinkedArea() {
		return Data.getObject(linkedExitID).getArea();
	}
	
	public void unlock() {
		this.isLocked = false;
		((ObjectExit) Data.getObject(linkedExitID)).isLocked = false;
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
	
}
