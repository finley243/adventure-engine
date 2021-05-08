package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.Item;

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
	public String getPrompt() {
		return "Take " + item.getName();
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
