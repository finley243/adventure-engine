package personal.finley.adventure_engine_2.action.item;

import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.world.object.item.Item;

public class ActionItemTake implements IAction {

	private Item item;
	private String objectID;
	
	public ActionItemTake(Item item, String objectID) {
		this.item = item;
		this.objectID = objectID;
	}
	
	@Override
	public void choose(Actor subject) {
		//subject.getArea().removeObject(item);
		//subject.inventory().addItem(item);
	}
	
	@Override
	public String getChoiceName() {
		return "Take " + item.getName();
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
