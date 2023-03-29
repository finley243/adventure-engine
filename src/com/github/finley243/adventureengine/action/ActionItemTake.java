package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionItemTake extends Action {

	private final Area area;
	private final Item item;
	
	public ActionItemTake(Area area, Item item) {
		this.area = area;
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		area.getInventory().removeItem(item);
		subject.getInventory().addItem(item);
		Context context = new Context(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if (item instanceof ItemWeapon && subject.isInCombat() && !subject.hasWeapon()) {
			return 0.7f;
		} else {
			return 0.0f;
		}
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		int count = area.getInventory().itemCount(item);
		return new MenuChoice("Take", canChoose(subject), new String[]{"ground", item.getName() + (count > 1 ? " (" + count + ")" : "")}, new String[]{"take " + item.getName(), "pick up " + item.getName(), "pickup " + item.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemTake other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }
	
}
