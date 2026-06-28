package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.PluralNoun;

public class ActionItemDropAll extends Action {

	private final Item item;

	public ActionItemDropAll(Actor subject, ActionDependencies dependencies, Item item) {
        super(subject, dependencies);
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionItemDropAll on item with state");
		this.item = item;
	}

	@Override
	public String getID() {
		return "item_drop_all";
	}

	@Override
	public Context getContext() {
		int count = subject.getInventory().itemCount(item);
		Context context = Context.builder().subject(subject).build();
		context.setLocalVariable("count", Expression.integer(count));
		context.setLocalVariable("item", Expression.noun(new PluralNoun(item, count)));
		return context;
	}
	
	@Override
	public void choose(int repeatActionCount) {
		int count = subject.getInventory().itemCount(item);
		subject.getInventory().removeItems(item.getTemplateID(), count);
		subject.getArea().getInventory().addItems(item.getTemplateID(), count);
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
		return "Drop All";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemDropAll other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }
	
}
