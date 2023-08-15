package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;

public class ActionItemDropAll extends Action {

	private final Item item;

	public ActionItemDropAll(Item item) {
		if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionItemDropAll on item with state");
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		int count = subject.getInventory().itemCount(item);
		subject.getInventory().removeItems(item.getTemplateID(), count);
		subject.getArea().getInventory().addItems(item.getTemplateID(), count);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, this, null, subject, null));
		subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Drop all", canChoose(subject).canChoose(), new String[]{"Inventory", Inventory.getItemNameFormatted(item, subject.getInventory())}, new String[]{"drop all " + LangUtils.pluralizeNoun(item.getName())});
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
