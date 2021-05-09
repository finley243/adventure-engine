package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionEquip implements Action {

	private ItemWeapon item;
	
	public ActionEquip(ItemWeapon item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.setEquippedItem(item);
		subject.inventory().removeItem(item);
		Context context = new Context(subject, item);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("equip"), context));
	}

	@Override
	public String getPrompt() {
		return "Equip " + item.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0;
	}

	@Override
	public String[] getMenuStructure() {
		return new String[] {"Inventory", LangUtils.titleCase(item.getName())};
	}

	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}

}
