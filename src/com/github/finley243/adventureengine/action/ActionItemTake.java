package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataItemWorld;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;
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
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
		(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, true, this, null, subject, null)).execute(subject.game());
		subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
	}

	@Override
	public float utility(Actor subject) {
		if (item instanceof ItemWeapon && subject.isInCombat() && !UtilityUtils.actorHasWeapon(subject)) {
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
