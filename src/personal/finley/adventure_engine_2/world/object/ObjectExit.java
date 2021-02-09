package personal.finley.adventure_engine_2.world.object;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.action.move.ActionMoveExit;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.world.environment.Area;

public class ObjectExit extends ObjectBase {

	private String linkedExitID;
	private boolean isLocked;
	
	public ObjectExit(String ID, String name, String linkedExitID) {
		super(ID, name);
		this.linkedExitID = linkedExitID;
	}
	
	public Area getLinkedArea() {
		return Data.getObject(linkedExitID).getArea();
	}
	
	@Override
	public List<IAction> localActions(Actor subject) {
		List<IAction> actions = new ArrayList<IAction>();
		if(isLocked) {
			
		} else {
			actions.add(new ActionMoveExit(this));
		}
		return actions;
	}
	
}
