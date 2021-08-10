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
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(!(action instanceof ActionAttackMelee)) {
			return false;
		} else {
			return ((ActionAttackMelee) action).weapon == this.weapon;
		}
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
	
	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionAttackMelee)) {
            return false;
        } else {
            ActionAttackMelee other = (ActionAttackMelee) o;
            return other.weapon == this.weapon && other.target == this.target;
        }
    }

}
