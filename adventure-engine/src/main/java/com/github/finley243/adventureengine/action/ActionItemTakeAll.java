package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataItemWorld;
import com.github.finley243.adventureengine.textgen.PluralNoun;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionItemTakeAll extends Action {

	private final Area area;
	private final Item item;

	public ActionItemTakeAll(Actor subject, ActionDependencies dependencies, Area area, Item item) {
        super(subject, dependencies);
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionItemTakeAll on item with state");
		this.area = area;
		this.item = item;
	}

	@Override
	public String getID() {
		return "item_take_all";
	}

	@Override
	public Context getContext() {
		int count = area.getInventory().itemCount(item);
		Context context = Context.builder().subject(subject).build();
		context.setLocalVariable("count", Expression.integer(count));
		context.setLocalVariable("item", Expression.noun(new PluralNoun(item, count)));
		return context;
	}
	
	@Override
	public void choose(int repeatActionCount) {
		int count = area.getInventory().itemCount(item);
		area.getInventory().removeItems(item.getTemplateID(), count);
		subject.getInventory().addItems(item.getTemplateID(), count);
		Context context = getContext();
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), "@pickUp", context, true, this, null));
	}

	@Override
	public int actionPoints() {
		return 0;
	}

	@Override
	public MenuData getMenuData() {
		return new MenuDataItemWorld(item, area.getInventory());
	}

	@Override
	public String getPrompt() {
		return "Take All";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemTakeAll other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }
	
}
