package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionItemDrop implements Action {

	private Item item;
	
	public ActionItemDrop(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.getArea().addObject(item);
		subject.inventory().removeItem(item);
		Context context = new Context(subject, false, item, true);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("drop"), context));
	}
	
	@Override
	public String getPrompt() {
		return "Drop " + item.getFormattedName(false);
	}
	
	@Override
	public float utility(Actor subject) {
		return 0;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"Inventory", LangUtils.titleCase(item.getName())};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
}
