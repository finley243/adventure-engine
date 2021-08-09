package com.github.finley243.adventureengine.action;

import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionAttackRanged implements Action {

	private ItemWeapon weapon;
	private Actor target;
	
	public ActionAttackRanged(ItemWeapon weapon, Actor target) {
		this.weapon = weapon;
		this.target = target;
	}
	
	@Override
	public void choose(Actor subject) {
		weapon.consumeAmmo(1);
		target.addCombatTarget(subject);
		if(ThreadLocalRandom.current().nextFloat() < weapon.getHitChance(subject)) {
			Context context = new Context(subject, false, target, false, weapon, false);
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("rangedHit"), context));
			target.damage(weapon.getDamage());
		} else {
			Context context = new Context(subject, false, target, false, weapon, false);
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("rangedMiss"), context));
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
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public int actionCount() {
		return weapon.getRate();
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
