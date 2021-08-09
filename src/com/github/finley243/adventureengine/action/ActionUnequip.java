package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionUnequip implements Action {

	private ItemWeapon item;
	
	public ActionUnequip(ItemWeapon item) {
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
	public int actionCount() {
		return 1;
	}

	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataEquipped("Unequip", item);
	}
	
}
