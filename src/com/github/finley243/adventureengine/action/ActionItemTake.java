package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataItemWorld;
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
	public String getID() {
		return "item_take";
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		area.getInventory().removeItem(item);
		subject.getInventory().addItem(item);
		Context context = new Context(subject.game(), subject, null, item);
		SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, true, this, null));
	}

	@Override
	public float utility(Actor subject) {
		if (item.hasComponentOfType(ItemComponentWeapon.class) && subject.isInCombat() && !UtilityUtils.actorHasWeapon(subject)) {
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
	public MenuData getMenuData(Actor subject) {
		return new MenuDataItemWorld(item, area.getInventory());
	}

	@Override
	public String getPrompt(Actor subject) {
		return "Take";
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
