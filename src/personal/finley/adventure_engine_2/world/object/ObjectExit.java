package personal.finley.adventure_engine_2.world.object;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.action.ActionUnlockExit;
import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.action.move.ActionMoveExit;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.world.environment.Area;

public class ObjectExit extends ObjectBase {

	private String linkedExitID;
	private boolean isLocked;
	
	public ObjectExit(String ID, String currentAreaID, String name, String linkedExitID) {
		super(ID, currentAreaID, name);
		this.linkedExitID = linkedExitID;
		
		this.isLocked = true; // FOR TESTING PURPOSES ONLY
	}
	
	@Override
	public String getFormattedName() {
		return super.getFormattedName() + " to " + getLinkedArea().getRoom().getFormattedName();
	}
	
	public Area getLinkedArea() {
		return Data.getObject(linkedExitID).getArea();
	}
	
	public void unlock() {
		this.isLocked = false;
		((ObjectExit) Data.getObject(linkedExitID)).isLocked = false;
	}
	
	@Override
	public List<IAction> localActions(Actor subject) {
		List<IAction> actions = new ArrayList<IAction>();
		if(isLocked) {
			actions.add(new ActionUnlockExit(this));
		} else {
			actions.add(new ActionMoveExit(this));
		}
		return actions;
	}
	
}
