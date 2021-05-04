package personal.finley.adventure_engine.action;

import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.world.item.Item;

public class ActionItemTake implements Action {

	private Item item;
	
	public ActionItemTake(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.getArea().removeObject(item);
		subject.inventory().addItem(item);
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
