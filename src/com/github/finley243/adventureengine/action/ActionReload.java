package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReload implements Action {

	private ItemWeapon weapon;
	
	public ActionReload(ItemWeapon weapon) {
		this.weapon = weapon;
	}
	
	@Override
	public void choose(Actor subject) {
		weapon.reloadFull();
		Context context = new Context(subject, false, weapon, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("reload"), context));
	}

	@Override
	public String getPrompt() {
		return "Reload " + weapon.getName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.9f;
	}

	@Override
	public int actionPoints() {
		return 2;
	}

	@Override
	public String[] getMenuStructure() {
		return new String[] {LangUtils.titleCase(weapon.getName())};
	}

	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}

}
