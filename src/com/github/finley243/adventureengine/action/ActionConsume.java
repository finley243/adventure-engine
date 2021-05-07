package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
import com.github.finley243.adventureengine.world.item.ItemConsumable;

public class ActionConsume implements Action {

	private ItemConsumable item;
	
	public ActionConsume(ItemConsumable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.inventory().removeItem(item);
		Context context = new Context(subject, item, Benefitting.SUBJECT, false, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("consume"), context));
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
