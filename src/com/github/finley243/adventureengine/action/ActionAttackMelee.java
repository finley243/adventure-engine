package com.github.finley243.adventureengine.action;

import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionAttackMelee implements Action {

	private ItemWeapon weapon;
	private Actor target;
	
	public ActionAttackMelee(ItemWeapon weapon, Actor target) {
		this.weapon = weapon;
		this.target = target;
	}

	@Override
	public void choose(Actor subject) {
		target.addCombatTarget(subject);
		if(ThreadLocalRandom.current().nextFloat() < weapon.getHitChance(subject)) {
			Context context = new Context(subject, false, target, false, weapon, false);
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("meleeHit"), context));
			target.damage(weapon.getDamage());
		} else {
			Context context = new Context(subject, false, target, false, weapon, false);
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("meleeMiss"), context));
		}
	}

	@Override
	public String getPrompt() {
		return "Attack " + target.getName() + " with " + weapon.getName();
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(target)) return 0;
		return 0.8f;
	}
	
	@Override
	public int actionPoints() {
		return 1;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {LangUtils.titleCase(weapon.getName())};
	}

	@Override
	public ActionLegality getLegality() {
		return ActionLegality.HOSTILE;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataEquipped("Attack " + target.getName(), weapon);
	}
	
}