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

	public ActionItemDropAll(Item item) {
		if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionItemDropAll on item with state");
		this.item = item;
	}

	@Override
	public String getID() {
		return "item_drop_all";
	}

	@Override
	public Context getContext(Actor subject) {
		Context context = new Context(subject.game(), subject, null, item);
		context.setLocalVariable("count", Expression.constant(subject.getInventory().itemCount(item)));
		return context;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		int count = subject.getInventory().itemCount(item);
		subject.getInventory().removeItems(item.getTemplateID(), count);
		subject.getArea().getInventory().addItems(item.getTemplateID(), count);
		Context context = new Context(subject.game(), subject, null, item);
		//TextContext textContext = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).build());
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
