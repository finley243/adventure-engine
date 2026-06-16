package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.WeaponItemComponent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataItemWorld;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionItemTake extends Action {

	private final Area area;
	private final Item item;
	
	public ActionItemTake(Actor subject, ActionDependencies dependencies, Area area, Item item) {
        super(subject, dependencies);
        this.area = area;
		this.item = item;
	}

	@Override
	public String getID() {
		return "item_take";
	}

	@Override
	public Context getContext() {
		return Context.builder().subject(subject).parentItem(item).build();
	}
	
	@Override
	public void choose(int repeatActionCount) {
		area.getInventory().removeItem(item);
		subject.getInventory().addItem(item);
		Context context = getContext();
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, true, this, null));
	}

	@Override
	public float utility() {
		if (item.hasComponentOfType(WeaponItemComponent.class) && subject.isInCombat() && !UtilityUtils.actorHasWeapon(subject)) {
			return 0.7f;
		} else {
			return 0.0f;
		}
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
