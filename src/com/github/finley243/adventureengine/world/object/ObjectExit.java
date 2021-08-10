package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveExit;
import com.github.finley243.adventureengine.action.ActionUnlockExit;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectExit extends LinkedObject {

	private String linkedExitID;
	private boolean isLocked;
	private String keyID;
	
	public ObjectExit(String ID, String name, String description, String linkedExitID, String keyID) {
		super(ID, name, description);
		this.linkedExitID = linkedExitID;
		this.keyID = keyID;
		this.isLocked = keyID != null;
	}
	
	public Area getLinkedArea() {
		return Data.getLinkedObject(linkedExitID).getArea();
	}
	
	public void unlock() {
		this.isLocked = false;
		((ObjectExit) Data.getLinkedObject(linkedExitID)).isLocked = false;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if(isLocked) {
			if(subject.inventory().hasItemWithID(keyID)) {
				actions.add(new ActionUnlockExit(this));
			}
		} else {
			actions.add(new ActionMoveExit(this));
		}
		return actions;
	}
	
}
