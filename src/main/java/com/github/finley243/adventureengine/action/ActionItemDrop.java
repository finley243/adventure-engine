package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionItemDrop extends Action {

	private final Item item;

	public ActionItemDrop(Item item) {
		this.item = item;
	}

	@Override
	public String getID() {
		return "item_drop";
	}

	@Override
	public Context getContext(Actor subject) {
		return new Context(subject.game(), subject, null, item);
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getInventory().removeItem(item);
		subject.getArea().getInventory().addItem(item);
		Context context = new Context(subject.game(), subject, null, item);
		SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, true, this, null));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataInventory(item, subject.getInventory());
	}

	@Override
	public String getPrompt(Actor subject) {
		return "Drop";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemDrop other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }
	
}
