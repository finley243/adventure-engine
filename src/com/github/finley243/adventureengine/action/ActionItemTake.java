package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
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
		Context context = new Context(subject, item);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("pickUp"), context));
	}
	
	@Override
	public String getPrompt() {
		return "Take " + item.getFormattedName();
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"World", LangUtils.titleCase(item.getName())};
	}
	
}
