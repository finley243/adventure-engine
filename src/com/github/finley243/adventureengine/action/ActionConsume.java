package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.ItemConsumable;

public class ActionConsume implements Action {

	private ItemConsumable item;
	
	public ActionConsume(ItemConsumable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.inventory().removeItem(item);
		
	}

	@Override
	public String getChoiceName() {
		return "Consume " + item.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}
