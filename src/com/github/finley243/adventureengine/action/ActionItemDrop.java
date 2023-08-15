package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionItemDrop extends Action {

	private final Item item;

	public ActionItemDrop(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getInventory().removeItem(item);
		subject.getArea().getInventory().addItem(item);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
		subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, this, null, subject, null));
		subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Drop", canChoose(subject).canChoose(), new String[]{"Inventory", Inventory.getItemNameFormatted(item, subject.getInventory())}, new String[]{"drop " + item.getName()});
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
