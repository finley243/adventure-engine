package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionItemDrop extends Action {

	private final Item item;
	private final boolean isEquipped;
	
	public ActionItemDrop(Item item, boolean isEquipped) {
		this.item = item;
		this.isEquipped = isEquipped;
	}
	
	@Override
	public void choose(Actor subject) {
		if(isEquipped) {
			subject.setEquippedItem(null);
		} else {
			subject.inventory().removeItem(item);
		}
		subject.getArea().addObject(item);
		item.setArea(subject.getArea());
		Context context = new Context(subject, false, item, true);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("drop"), context, this, subject));
	}

	@Override
	public int actionPoints() {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Drop", "Drop " + item.getFormattedName(false), canChoose(subject), new String[]{"inventory", item.getName() + subject.inventory().itemCountLabel(item.getStatsID())});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemDrop)) {
            return false;
        } else {
            ActionItemDrop other = (ActionItemDrop) o;
            return other.item == this.item;
        }
    }
	
}
