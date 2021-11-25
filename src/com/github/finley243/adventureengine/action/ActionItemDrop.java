package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionItemDrop implements Action {

	private boolean disabled;
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
		Context context = new Context(subject, false, item, true);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("drop"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	@Override
	public void disable() {
		disabled = true;
	}
	
	@Override
	public String getPrompt() {
		return "Drop " + item.getFormattedName(false);
	}
	
	@Override
	public float utility(Actor subject) {
		return 0;
	}
	
	@Override
	public boolean usesAction() {
		return false;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		//if(isEquipped) {
		//	return new MenuDataEquipped("Drop", item);
		//} else {
			return new MenuDataInventory("Drop", canChoose(subject), item);
		//}
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
