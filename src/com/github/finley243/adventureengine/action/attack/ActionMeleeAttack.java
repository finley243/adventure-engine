package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.concurrent.ThreadLocalRandom;

public class ActionMeleeAttack extends ActionAttack {

	public ActionMeleeAttack(ItemWeapon weapon, Actor target) {
		super(weapon, target);
	}

	@Override
	public void onStart(Actor subject) {
		getTarget().addCombatTarget(subject);
		Context attackContext = new Context(subject, getTarget(), getWeapon());
		if(!CombatHelper.isRepeat(attackContext)) {
			subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get(CombatHelper.getTelegraphPhrase(getWeapon(), null, false)), attackContext, null, null));
		}
	}

	@Override
	public void onSuccess(Actor subject) {
		int damage = weapon.getDamage();
		boolean crit = false;
		if(ThreadLocalRandom.current().nextFloat() < ItemWeapon.CRIT_CHANCE) {
			damage += weapon.getCritDamage();
			crit = true;
		}
		Context attackContext = new Context(subject, target, weapon);
		String hitPhrase = CombatHelper.getHitPhrase(weapon, null, crit, false);
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get(hitPhrase), attackContext, null, null));
		target.damage(damage);
	}

	@Override
	public void onFail(Actor subject) {
		Context attackContext = new Context(subject, getTarget(), getWeapon());
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get(CombatHelper.getMissPhrase(getWeapon(), null, false)), attackContext, this, subject));
	}

	@Override
	public float chance(Actor subject) {
		return CombatHelper.calculateHitChance(subject, getTarget(), null, getWeapon(), false);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && subject.getArea() == getTarget().getArea() && subject.canSee(getTarget());
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(getTarget())) return 0;
		return 0.8f;
	}

	@Override
	public int repeatCount(Actor subject) {
		return weapon.getRate();
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(action instanceof ActionMeleeAttack) {
			return ((ActionMeleeAttack) action).getWeapon() == this.getWeapon();
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + (int) Math.ceil(chance(subject)*100) + "%)", canChoose(subject), new String[]{weapon.getName() + " (equipped)", getTarget().getName()});
	}

}
