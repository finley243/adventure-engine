package personal.finley.adventure_engine.world.item;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine.action.IAction;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.world.object.ObjectBase;

public abstract class Item extends ObjectBase {
	
	public Item(String ID, String areaID, String name) {
		super(ID, areaID, name);
	}

	@Override
	public List<IAction> localActions(Actor subject) {
		return new ArrayList<IAction>();
	}
	
	@Override
	public List<IAction> remoteActions(Actor subject) {
		return new ArrayList<IAction>();
	}
	
	public List<IAction> inventoryActions(Actor subject) {
		return new ArrayList<IAction>();
	}
	
}
