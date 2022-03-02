package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemEquippable;

public class ActionItemUnequip extends Action {

	private final ItemEquippable item;
	
	public ActionItemUnequip(ItemEquippable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.setEquippedItem(null);
		subject.inventory().addItem(item);
		Context context = new Context(subject, item);
		subject.game().eventBus().post(new VisualEvent(subject.getArea(), Phrases.get("unequip"), context, this, subject));
	}

	@Override
	public float utility(Actor subject) {
		if(subject.isInCombat()) {
			return 0;
		}
		return 0.5f;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Unequip", canChoose(subject), new String[]{"inventory", item.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemUnequip)) {
            return false;
        } else {
            ActionItemUnequip other = (ActionItemUnequip) o;
            return other.item == this.item;
        }
    }
	
}
