package personal.finley.adventure_engine_2.world.object.item;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.world.object.ObjectBase;

public class Item extends ObjectBase {
	
	public Item(String ID, String name) {
		super(ID, name);
		//Data.addItem(ID, this);
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
