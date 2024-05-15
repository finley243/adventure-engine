package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataItemWorld;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionItemTakeAll extends Action {

	private final Area area;
	private final Item item;

	public ActionItemTakeAll(Area area, Item item) {
		if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionItemTakeAll on item with state");
		this.area = area;
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		int count = area.getInventory().itemCount(item);
		area.getInventory().removeItems(item.getTemplateID(), count);
		subject.getInventory().addItems(item.getTemplateID(), count);
		Context context = new Context(subject.game(), subject, null, item);
		//TextContext textContext = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, true, this, null));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataItemWorld(item, area.getInventory());
	}

	@Override
	public String getPrompt(Actor subject) {
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
