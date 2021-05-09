package com.github.finley243.adventureengine.action;

import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
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
		if(ThreadLocalRandom.current().nextFloat() < weapon.getHitChance()) {
			Context context = new Context(subject, target, weapon);
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("rangedHit"), context));
			target.damage(weapon.getDamage());
		} else {
			Context context = new Context(subject, target, weapon);
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("rangedMiss"), context));
		}
	}

	@Override
	public String getPrompt() {
		return "Attack " + target.getName() + " with " + weapon.getName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {LangUtils.titleCase(weapon.getName())};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.HOSTILE;
	}
	
}
