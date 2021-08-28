package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemEquippable;

public class ActionUnequip implements Action {

	private ItemEquippable item;
	
	public ActionUnequip(ItemEquippable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.setEquippedItem(null);
		subject.inventory().addItem(item);
		Context context = new Context(subject, false, item, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("unequip"), context));
	}

	@Override
	public String getPrompt() {
		return "Unequip " + item.getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		if(subject.isInCombat()) {
			return 0;
		}
		return 0.5f;
	}
	
	@Override
	public boolean usesAction() {
		return true;
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
	public MenuData getMenuData() {
		return new MenuDataEquipped("Unequip", item);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionUnequip)) {
            return false;
        } else {
            ActionUnequip other = (ActionUnequip) o;
            return other.item == this.item;
        }
    }
	
}
