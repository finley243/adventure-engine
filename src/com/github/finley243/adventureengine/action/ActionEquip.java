package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
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
		Context context = new Context(subject, false, item, true);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("equip"), context));
	}

	@Override
	public String getPrompt() {
		return "Equip " + item.getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		if(!subject.isInCombat()) return 0;
		return 1;
	}
	
	@Override
	public int actionPoints() {
		return 1;
	}

	@Override
	public String[] getMenuStructure() {
		return new String[] {"Inventory", LangUtils.titleCase(item.getName())};
	}

	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataInventory("Equip", item);
	}

}
