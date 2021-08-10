package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Context;
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
	public boolean usesAction() {
		return false;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataInventory("Drop", item);
	}
	
}
