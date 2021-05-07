package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
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
	
	public ObjectExit(String ID, String name, String linkedExitID) {
		super(ID, name);
		this.linkedExitID = linkedExitID;
		
		this.isLocked = true; // FOR TESTING PURPOSES ONLY
	}
	
	@Override
	public String getFormattedName() {
		return super.getFormattedName() + " to " + getLinkedArea().getRoom().getFormattedName();
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
		List<Action> actions = new ArrayList<Action>();
		if(isLocked) {
			actions.add(new ActionUnlockExit(this));
		} else {
			actions.add(new ActionMoveExit(this));
		}
		return actions;
	}
	
}
