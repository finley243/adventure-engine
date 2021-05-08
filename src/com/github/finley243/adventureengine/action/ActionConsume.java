package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemConsumable;

public class ActionConsume implements Action {

	private ItemConsumable item;
	
	public ActionConsume(ItemConsumable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.inventory().removeItem(item);
		Context context = new Context(subject, item);
		switch(item.getConsumableType()) {
		case DRINK:
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("drink"), context));
			break;
		case FOOD:
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("eat"), context));
			break;
		case OTHER:
		default:
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("consume"), context));
			break;
		}
	}

	@Override
	public String getPrompt() {
		switch(item.getConsumableType()) {
		case DRINK:
			return "Drink " + item.getFormattedName();
		case FOOD:
			return "Eat " + item.getFormattedName();
		case OTHER:
		default:
			return "Use " + item.getFormattedName();
		}
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"Inventory", LangUtils.capitalize(item.getName())};
	}

}
