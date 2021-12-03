package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionRangedAttack extends ActionAttack {
	
	public ActionRangedAttack(ItemWeapon weapon, Actor target) {
		super(weapon, target);
	}

	@Override
	public void onStart(Actor subject) {
		getWeapon().consumeAmmo(1);
		Game.EVENT_BUS.post(new SoundEvent(subject.getArea(), true));
		getTarget().addCombatTarget(subject);
		Context attackContext = new Context(subject, false, getTarget(), false, getWeapon(), false);
		if(!CombatHelper.isRepeat(attackContext)) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(CombatHelper.getTelegraphPhrase(getWeapon(), null, false)), attackContext, null, null));
		}
	}

	@Override
	public void onSuccess(Actor subject) {
		CombatHelper.handleHit(subject, getTarget(), null, getWeapon(), false);
	}

	@Override
	public void onFail(Actor subject) {
		Context attackContext = new Context(subject, false, getTarget(), false, getWeapon(), false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(CombatHelper.getMissPhrase(getWeapon(), null, false)), attackContext, this, subject));
	}

	@Override
	public float chance(Actor subject) {
		return CombatHelper.calculateHitChance(subject, getTarget(), null, getWeapon(), false);
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
	public int multiCount() {
		return getWeapon().getRate();
	}

	@Override
	public boolean isMultiMatch(Action action) {
		if(action instanceof ActionRangedAttack) {
			return ((ActionRangedAttack) action).getWeapon() == this.getWeapon();
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + LangUtils.titleCase(getWeapon().getName()) + ")", "Attack " + getTarget().getFormattedName(false) + " with " + getWeapon().getFormattedName(false), canChoose(subject), new String[]{getTarget().getName()});
	}

}
