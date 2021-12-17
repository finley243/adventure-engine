package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ActionRangedAttackTargeted extends ActionAttack {

	private final Limb limb;

	public ActionRangedAttackTargeted(ItemWeapon weapon, Actor target, Limb limb) {
		super(weapon, target);
		this.limb = limb;
	}

	@Override
	public void onStart(Actor subject) {
		getWeapon().consumeAmmo(1);
		Game.EVENT_BUS.post(new SoundEvent(subject.getArea(), true));
		getTarget().addCombatTarget(subject);
		Context attackContext = new Context(subject, false, getTarget(), false, getWeapon(), false);
		if(!CombatHelper.isRepeat(attackContext)) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(CombatHelper.getTelegraphPhrase(getWeapon(), limb, false)), attackContext, null, null));
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
		Context attackContext = new Context(subject, false, target, false, weapon, false);
		String hitPhrase = CombatHelper.getHitPhrase(weapon, limb, crit, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(hitPhrase), attackContext, null, null));
		target.damageLimb(damage, limb);
	}

	@Override
	public void onFail(Actor subject) {
		Context attackContext = new Context(subject, false, getTarget(), false, getWeapon(), false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(CombatHelper.getMissPhrase(getWeapon(), limb, false)), attackContext, this, subject));
	}

	@Override
	public float chance(Actor subject) {
		return CombatHelper.calculateHitChance(subject, getTarget(), limb, getWeapon(), false);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && getWeapon().getAmmoRemaining() >= 1 && subject.canSee(getTarget());
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(getTarget())) return 0;
		return 0.8f;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Targeted Attack (" + LangUtils.titleCase(getWeapon().getName()) + ", " + LangUtils.titleCase(limb.getName()) + ", " + (int) Math.ceil(chance(subject)*100) + "%)", "Attack " + getTarget().getFormattedName(false) + " with " + getWeapon().getFormattedName(false) + " (targeted: " + limb.getName() + ")", canChoose(subject), new String[]{getTarget().getName()});
	}

}
