package personal.finley.adventure_engine.world.item;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine.action.ActionItemTake;
import personal.finley.adventure_engine.action.Action;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.world.object.WorldObject;

public abstract class Item extends WorldObject {
	
	public Item(String ID, String areaID, String name) {
		super(ID, areaID, name);
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionItemTake(this));
		return actions;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public List<Action> inventoryActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
}
