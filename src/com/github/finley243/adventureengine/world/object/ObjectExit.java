package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionListenExit;
import com.github.finley243.adventureengine.action.ActionMoveExit;
import com.github.finley243.adventureengine.action.ActionUnlockExit;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectExit extends LinkedObject {

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
		return Data.getLinkedObject(linkedExitID).getArea();
	}
	
	public void unlock() {
		this.isLocked = false;
		((ObjectExit) Data.getLinkedObject(linkedExitID)).isLocked = false;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.add(new ActionListenExit(this));
		if(isLocked) {
			boolean hasKey = false;
			for(String keyID : keyIDs) {
				if(subject.inventory().hasItemWithID(keyID)) {
					hasKey = true;
					break;
				}
			}
			if(hasKey) {
				actions.add(new ActionUnlockExit(this));
			}
		} else {
			actions.add(new ActionMoveExit(this));
		}
		return actions;
	}
	
}
