package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionItemTake extends Action {

	private final Item item;
	
	public ActionItemTake(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		item.getArea().removeObject(item);
		subject.inventory().addItem(item);
		Context context = new Context(subject, false, item, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("pickUp"), context, this, subject));
	}

	@Override
	public float utility(Actor subject) {
		if(item instanceof ItemWeapon && subject.isInCombat() && !subject.hasWeapon()) {
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
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Take", "Take " + item.getFormattedName(false), canChoose(subject), new String[]{item.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemTake)) {
            return false;
        } else {
            ActionItemTake other = (ActionItemTake) o;
            return other.item == this.item;
        }
    }
	
}
