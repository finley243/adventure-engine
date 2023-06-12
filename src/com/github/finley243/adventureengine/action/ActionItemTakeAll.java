package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionItemTakeAll extends Action {

	private final Area area;
	private final Item item;

	public ActionItemTakeAll(Area area, Item item) {
		if (item.getTemplate().hasState()) throw new IllegalArgumentException("Cannot perform ActionItemTakeAll on item with state");
		this.area = area;
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		int count = area.getInventory().itemCount(item);
		area.getInventory().removeItems(item.getTemplate().getID(), count);
		subject.getInventory().addItems(item.getTemplate().getID(), count);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, this, null, subject, null));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Take all", canChoose(subject), new String[]{"Ground", Inventory.getItemNameFormatted(item, area.getInventory())}, new String[]{"take all " + LangUtils.pluralizeNoun(item.getName()), "pick up all " + LangUtils.pluralizeNoun(item.getName()), "pickup all " + LangUtils.pluralizeNoun(item.getName())});
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
