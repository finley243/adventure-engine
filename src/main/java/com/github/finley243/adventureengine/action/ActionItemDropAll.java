package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Phrases;

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
		Context context = Context.builder().subject(subject).parentItem(item).build();
		context.setLocalVariable("count", Expression.integer(subject.getInventory().itemCount(item)));
		return context;
	}
	
	@Override
	public void choose(int repeatActionCount) {
		int count = subject.getInventory().itemCount(item);
		subject.getInventory().removeItems(item.getTemplateID(), count);
		subject.getArea().getInventory().addItems(item.getTemplateID(), count);
		Context context = getContext();
		//TextContext textContext = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, true, this, null));
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
