package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;

public class ActionItemDrop extends Action {

	private final Item item;

	public ActionItemDrop(Actor subject, ActionDependencies dependencies, Item item) {
        super(subject, dependencies);
        this.item = item;
	}

	@Override
	public String getID() {
		return "item_drop";
	}

	@Override
	public Context getContext() {
		return Context.builder().subject(subject).parentItem(item).build();
	}
	
	@Override
	public void choose(int repeatActionCount) {
		subject.getInventory().removeItem(item);
		subject.getArea().getInventory().addItem(item);
		Context context = getContext();
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), "@drop", context, true, this, null));
	}

	@Override
	public int actionPoints() {
		return 0;
	}

	@Override
	public MenuData getMenuData() {
		return new MenuDataInventory(item, subject.getInventory());
	}

	@Override
	public String getPrompt() {
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
